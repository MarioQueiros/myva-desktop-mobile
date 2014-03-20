// Creating the database and parameters
var baseURL = 'https://mpapq.myftp.org/api/api';
var db = openDatabase('MyVa', '1.0', 'MyVa DB', 2 * 1024 * 1024);
//
var availableLocals = [];
var availableLocalsIds = [];
var availableLocalsCords = [];
//
var eventsName = [];
var eventsPlaces = [];
var eventsDate = [];

// Creates the table for Users
db.transaction(function (tx) {
    tx.executeSql("CREATE TABLE IF NOT EXISTS " +
                  "Users(ID INTEGER PRIMARY KEY ASC, priv_key TEXT, username TEXT)", []);
});

//HTTP Basic Authentication for Ajax Request Header
function make_base_auth(user, password) {
    var tok = user + ':' + password;
    var hash = btoa(tok);
    return "Basic " + hash;
}

// Cypher Methods
function getHMAC(timestamp, u, callback) {
    var pubKey = "";
    var privKey = "";
    var finished = true;
    var hash = "";

    if (hash == "") {
        pubKey = getCookie('pub_key');

        db.transaction(function (tx) {
            tx.executeSql('SELECT priv_key FROM Users WHERE username=?', [u], function (tx, results) {
                if (results.rows.length > 0) {
                    privKey = results.rows.item(0).priv_key;
                }

                if (privKey != "" && pubKey != "") {
                    hash = CryptoJS.HmacSHA1(pubKey + timestamp, privKey);
                    callback(finished, hash.toString());
                } else {
                    callback(finished, "");
                }

            });
        });
    } else {
        finished = false;
        callback(finished, hash.toString());
    }
}

var getHMACTest = function (username, timestamp, privKey) {
    var hash = CryptoJS.HmacSHA1(username + timestamp, privKey);
    return hash.toString();
};

var getMicrotime = function (get_as_float) {
    var now = new Date().getTime() / 1000;
    var s = parseInt(now, 10);
    return (get_as_float) ? now : (Math.round((now - s) * 1000) / 1000) + ' ' + s;
};


/* ==========================================================================
   Connections
   ========================================================================== */

function syncronizeFromServer(u, pub, callback) {
    var timestamp = getMicrotime(true).toString();

    getHMAC(timestamp, u, function (finished, hash) {
        if (finished) {
            if (hash != "") {
                clearEvents();
                getLocals(u, pub, hash, timestamp, function (type) {
                    if(type){
                        getEvents(u, pub, hash, timestamp, function (type) {

                            showNoEvents(!type);

                            if(type)
                                callback(true, "all");
                            else
                                callback(true, "locals");
                        });
                    } else {
                        callback(true, "none");
                    }
                });
            } else {
                callback(false, "");
            }
        }
    });
}

function addEvent(u, p, b, callback) {
    var timestamp = getMicrotime(true).toString();

    getHMAC(timestamp, getCookie('username'), function (finished, hash) {
        if (finished) {
            $.ajax({
                type: "POST",
                url: baseURL + "/addevent",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: true,
                beforeSend: function (req) {

                    req.setRequestHeader('X-USERNAME', getCookie('username'));
                    req.setRequestHeader('X-NAME', u);
                    req.setRequestHeader('X-DATE', new Date(b).getTime());

                    if(newLocal){
                        req.setRequestHeader('X-LOCALNAME', newMarkerName);
                        req.setRequestHeader('X-LATITUDE', newMarkerLatitude);
                        req.setRequestHeader('X-LONGITUDE', newMarkerLongitude);
                    }else{
                        req.setRequestHeader('X-LOCALID', availableLocalsIds[selectedIndex]);
                    }

                    req.setRequestHeader('X-PUBKEY', getCookie('pub_key'));
                    req.setRequestHeader('X-MICROTIME', timestamp);
                    req.setRequestHeader('X-HASH', hash.toString());
                },
                success: function (data) {
                    syncronizeFromServer(getCookie('username'), getCookie('pub_key'), function (type, loaded) {
                        // Animation stuff
                        leaveCreateEvent(250);

                        callback(type);
                    });
                },
                error: function (data, textStatus, jqXHR) {
                    callback(false);
                }
            });
        }
    });
}

function register(u, p, e, b, callback) {

    var timestamp = getMicrotime(true).toString();

    $.ajax({
        type: "POST",
        url: baseURL + "/register",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        crossDomain: true,
        data: JSON.stringify({ username: u, password: p, email: e, birthday: b }),
        beforeSend: function (req) {
            req.setRequestHeader('X-MICROTIME', timestamp);
        },
        success: function (data) {

            db.transaction(function (tx) {
                //code for save user username for hmac encription
                tx.executeSql("INSERT INTO Users (priv_key, username) VALUES (?,?)",
                    ["", u]);

                callback(true);
            });
        },
        error: function (data, textStatus, jqXHR) {

            callback(false);
        }
    });
}

function login(u, p, auth, callback) {

    var timestamp = getMicrotime(true).toString();

    $.ajax({
        type: "POST",
        url: baseURL + "/login",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({ username: u, password: p }),
        beforeSend: function (req) {
            req.setRequestHeader('Authentication', auth);
            req.setRequestHeader('X-MICROTIME', timestamp);
        },
        success: function (output, status, xhr) {
            var json = JSON.parse(xhr.getResponseHeader('X-Status-Reason'));

            db.transaction(function (tx) {

                tx.executeSql('SELECT * FROM Users WHERE username=?', [u], function (tx, results) {
                    //code for save user public key for hmac encription
                    if (results.rows.length == 0) {
                        tx.executeSql("INSERT INTO Users(username) VALUES (?)",
                            [u]);

                        createCookie("pub_key", json.PubKey, 0);

                        // Not present in database
                        callback(u, false);

                    } else {
                        createCookie("pub_key", json.PubKey, 0);

                        // Present in database
                        db.transaction(function (tx) {
                            tx.executeSql('SELECT priv_key FROM Users WHERE username=?', [u], function (tx, results) {
                                if (results.rows.length == 0) {
                                    callback(u, false);
                                } else {
                                    if (results.rows.item(0).priv_key != null) {
                                        syncronizeFromServer(u, json.PubKey, function (type, loaded) {
                                            // loaded == "", "none", "locals", "all"
                                            if (type && loaded != "") {
                                                callback(u, true);
                                            } else {
                                                callback(u, false);
                                            }
                                        });
                                    } else {
                                        callback(u, false);
                                    }
                                }
                            });
                        });
                    }
                });
            });
        },
        error: function (data, textStatus, jqXHR) {
            var json = JSON.parse(data.getResponseHeader('X-Status-Reason'));
            if (json.Error == "Please Confirm the account before you login.") {
                
                db.transaction(function (tx) {
                    tx.executeSql("INSERT INTO Users(username) VALUES (?)", [u]);

                    createCookie("pub_key", json.PubKey, 0);
                    callback("", true);
                });

            } else {
                callback("", false);
            }
        }
    });
}

// TODO
function recover(u, m, callback) {

}

function confirm(u, c, callback) {

    var timestamp = getMicrotime(true).toString();

    $.ajax({
        type: "GET",
        url: baseURL + "/test",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (request) {
            request.setRequestHeader('X-MICROTIME', timestamp);
            request.setRequestHeader('X-HASH', getHMACTest(u, timestamp, c));
            request.setRequestHeader('X-USERNAME', u);
            request.setRequestHeader('X-RESENDTYPE', reconf);
        },
        data: "",
        success: function (data) {
            db.transaction(function (tx) {
                tx.executeSql('UPDATE Users SET priv_key="' + c + '" WHERE username="' + u + '"');

                syncronizeFromServer(u, getCookie('pub_key'), function (type, loaded) {
                        // loaded == "", "none", "locals", "all"
                    if (type && loaded != "") {
                        callback(true);
                    } else {
                        callback(false);
                    }
                });
            });
        },
        error: function (data, textStatus, jqXHR) {
            callback(false);
        }
    });
}

function getLocals(u, pub, hash, timestamp, callback) {

    $.ajax({
        type: "GET",
        url: baseURL + "/getprivatelocals",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (req) {
            req.setRequestHeader('X-PUBKEY', pub);
            req.setRequestHeader('X-USERNAME', u);
            req.setRequestHeader('X-MICROTIME', timestamp);
            req.setRequestHeader('X-HASH', hash.toString());
        },
        success: function (output, status, xhr) {
            var json = JSON.parse(xhr.getResponseHeader('X-Status-Reason'));

            availableLocals = [];
            availableLocalsIds = [];
            availableLocalsCords = [];

            for (var i = 0; i < json.privateLocals.length; i++) {
                availableLocals.push(json.privateLocals[i].name);
                availableLocalsIds.push(json.privateLocals[i].id);
                availableLocalsCords.push(json.privateLocals[i].latitude + ";" + json.privateLocals[i].longitude);
            }

            var select = document.getElementById("input-place-name");
            for (index in availableLocals) {
                select.options[select.options.length] = new Option(availableLocals[index], index);
            }


            callback(true);
        },
        error: function (data, textStatus, jqXHR) {

            callback(false);
        }
    });
}

function getEvents(u, pub, hash, timestamp, callback) {

    $.ajax({
        type: "GET",
        url: baseURL + "/getallevents",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (req) {
            req.setRequestHeader('X-PUBKEY', pub);
            req.setRequestHeader('X-USERNAME', u);
            req.setRequestHeader('X-MICROTIME', timestamp);
            req.setRequestHeader('X-HASH', hash.toString());
        },
        success: function (output, status, xhr) {

            var json = JSON.parse(xhr.getResponseHeader('X-Status-Reason'));

            var eventsName = [];
            var eventsPlaces = [];
            var eventsDate = [];

            for (var i = 0; i < json.events.length; i++) {
                //// Names
                eventsName.push(json.events[i].name);

                //// Places
                var place = "";
                // Compare Ids
                var index = arraySearch(availableLocalsIds, json.events[i].local);
                if (index >= 0) {
                    place = availableLocals[index];
                }
                eventsPlaces.push(place);

                //// Date & Today
                var date = new Date(json.events[i].dateevent);
                var today = new Date();
                var todayBool = false;
                //
                if (date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && date.getFullYear() == today.getFullYear()) {
                    todayBool = true;
                }
                var curr_date = date.getDate();
                if (curr_date < 10)
                    curr_date = "0" + curr_date;
                var curr_month = date.getMonth() + 1;
                if (curr_month < 10)
                    curr_month = "0" + curr_month;
                var curr_year = date.getFullYear();
                //
                eventsDate.push(curr_month + "/" + curr_date + "/" + curr_year);
                //
                createEventDiv(eventsName[i], eventsPlaces[i], todayBool, eventsDate[i], json.events[i].id, i, json.events[i].local);
            }

            callback(true);
        },
        error: function (data, textStatus, jqXHR) {
            callback(false);
        }
    });
}
//
function arraySearch(arr, val) {
    for (var i = 0; i < arr.length; i++)
        if (arr[i] == val)
            return i;
    return -1;
}

function resendConfirm(u, callback) {

    var timestamp = getMicrotime(true).toString();

    $.ajax({
        type: "GET",
        url: baseURL + "/resendconfirmationemail",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (request) {
            request.setRequestHeader('X-MICROTIME', timestamp);
            request.setRequestHeader('X-USERNAME', u);
        },
        success: function (output, status, xhr) {
            callback(true);
        },
        error: function (data, textStatus, jqXHR) {
            callback(false);
        }
    });
}

function removeEvent(index, callback) {
    var timestamp = getMicrotime(true).toString();

    var eids = [];
    eids[0] = ["id",index];

    getHMAC(timestamp, getCookie('username'), function (finished, hash) {
        if (finished) {
            $.ajax({
                type: "POST",
                url: baseURL + "/deleteevent",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: true,
                data: JSON.stringify({ username: getCookie('username'), eventids: eids }),
                beforeSend: function (req) {
                    req.setRequestHeader('X-PUBKEY', getCookie('pub_key'));
                    req.setRequestHeader('X-MICROTIME', timestamp);
                    req.setRequestHeader('X-HASH', hash.toString());
                },
                success: function (data) {
                    clearEvents();
                    getEvents(getCookie('username'), getCookie('pub_key'), hash, timestamp, function (type) {
                        callback(type);
                    });
                },
                error: function (data, textStatus, jqXHR) {
                    callback(false);
                }
            });
        }
    });
}

/* ==========================================================================
   Cookies
   ========================================================================== */

// Get cookies
//
//
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}

// Create cookies
//
//
function createCookie(name, value, hours) {
    if (hours) {
        var date = new Date();
        date.setTime(date.getTime() + (hours * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else {
        var expires = "";
    }
    document.cookie = name + "=" + value + expires + "; path=/";
}

// nomnomnomnomnom
var username = getCookie("username");
var state = getCookie("app_state");
var key = getCookie("pub_key");

if (username != null) {
    if (username != "") {
        if (key != null) {
            if (key != "") {
                if (state != null) {
                    if (state != "confirm_account" && state != "") {

                        $("#spinner-div").fadeIn(100);
                        syncronizeFromServer(username, key, function (type, loaded) {
                            // loaded == "", "none", "locals", "all"
                            if (type && loaded != "") {
                                $("#spinnerText").text("Welcome " + capitaliseFirstLetter(username) + "!");
                                loginAnimation(0, username);
                            }
                            turnOfSpinner(false, function () {});
                        });
                    }
                    else{
                        createCookie("app_state", "", -1);
                        createCookie("pub_key", "", -1);
                    }
                }
            }
            else {
                createCookie("app_state", "", -1);
                createCookie("pub_key", "", -1);
            }
        }
        else {
            createCookie("app_state", "", -1);
            createCookie("pub_key", "", -1);
        }
    }
}