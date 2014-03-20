//HTTP Basic Authentication for Ajax Request Header
function make_base_auth(user, password) {
    var tok = user + ':' + password;
    var hash = btoa(tok);
    return "Basic " + hash;
}

$(function() {
    $("#Sbirth").datepicker();
});

var app = (function($) {

    var baseURL = 'https://mpapq.myftp.org/api/api';

    var db = openDatabase('MyVa', '1.0', 'MyVa DB', 2 * 1024 * 1024);

    db.transaction(function(tx) {
        tx.executeSql('CREATE TABLE IF NOT EXISTS Info (id unique, priv_key, pub_key, username)');
    });

    var init = function() {
        $('.loggedIn').hide();
        $('#login').on('click', function(e) {
            e.preventDefault();
            login();
        });
        $('.sendRequest').on('click', function(e) {
            e.preventDefault();
            testRequest();
        });
        $('#register').on('click', function(e) {
            e.preventDefault();
            register();
        });
        $('#confirm').on('click', function(e) {
            e.preventDefault();
            testRequest();
        });
    };

    var register = function()
    {
        var u = $('#Susername').val();
        var p = $('#Spassword').val();
        var e = $('#Semail').val();
        var b = $('#Sbirth').val();

        var timestamp = getMicrotime(true).toString();

        $.ajax({
            type: "POST",
            url: baseURL + "/register",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            crossDomain: true,
            data: JSON.stringify({username: u, password: p, email: e, birthday: b}),
            beforeSend: function(req) {

                req.setRequestHeader('X-MICROTIME', timestamp);
            },
            success: function(data) {
                alert('Success');
                $('.loggedOut').hide();
                $('.loggedIn').show();
                $('.loggedIn .name').text("Now please confirm the account!");

                db.transaction(function(tx) {
                    //code for save user username for hmac encription
                    tx.executeSql('INSERT INTO Info (id, priv_key, pub_key, username) VALUES (1, "", "", "' + u + '")');
                });
            },
            error: function(data, textStatus, jqXHR) {
                alert(data.getResponseHeader('X-Status-Reason'));
                alert(data.status + "\n" + jqXHR + "\n" + textStatus);
            }
        });
    };

    var login = function() {
        var u = $('#username').val();
        var p = $('#password').val();
        var auth = make_base_auth(u, p);
        var timestamp = getMicrotime(true).toString();
        $.ajax({
            type: "POST",
            url: baseURL + "/login",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify({username: u, password: p}),
            beforeSend: function(req) {
                req.setRequestHeader('Authentication', auth);
                req.setRequestHeader('X-MICROTIME', timestamp);
                req.setRequestHeader('X-HASH', getHMAC(timestamp));
            },
            success: function(output, status, xhr) {
                $('.loggedOut').hide();
                $('.loggedIn').show();
                var json = JSON.parse(xhr.getResponseHeader('X-Status-Reason'));
                $('.loggedIn .name').text("Success. PubKey: "+json.PubKey);
                
//                $('.loggedIn .name').text("PubKey: " + json.pubKey + ".");
                db.transaction(function(tx) {
                    //code for save user public key for hmac encription
                    tx.executeSql('UPDATE Info SET pub_key="' + json.PubKey + '" WHERE username="' + u + '"');
                });
            },
            error: function(data, textStatus, jqXHR) {
                alert(data.status + "\n" + jqXHR + "\n" + textStatus);
            }
        });
    };

    var testRequest = function() {
        var u = $('#Cusername').val();
        var c = $('#code').val();
        var timestamp = getMicrotime(true).toString();
        $.ajax({
            type: "GET",
            url: baseURL + "/test",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            beforeSend: function(request) {
                request.setRequestHeader('X-MICROTIME', timestamp);
                request.setRequestHeader('X-HASH', getHMACTest(u, timestamp, c));
                request.setRequestHeader('X-USERNAME', u);
            },
            data: JSON.stringify({username: u}),
            success: function(data) {
                alert("Success, User confirmed.");

                db.transaction(function(tx) {
                    //code for save user public key for hmac encription
                    tx.executeSql('UPDATE Info SET priv_key="' + c + '" WHERE username="' + u + '"');
                });
            },
            error: function(data, textStatus, jqXHR) {
                alert("X-Status-Reason:" + data.getResponseHeader('X-Status-Reason') + "\nStatus: " + data.status + "\n" + jqXHR + "\n" + textStatus);
            }
        });
    };



    var getHMAC = function(timestamp) {
        var privKey = 0;
        var pubKey = 0;
        var db = openDatabase('MyVa', '1.0', 'MyVa DB', 2 * 1024 * 1024);
        db.transaction(function(tx) {
            tx.executeSql('SELECT priv_key FROM Info', [], function(tx, results) {
                privKey = results.rows.item(0).priv_key;
            });
        });
        if (privKey != 0)
        {
            db.transaction(function(tx) {
                tx.executeSql('SELECT pub_key FROM Info', [], function(tx, results) {
                    pubKey = results.rows.item(0).pub_key;
                });
            });
            if (pubKey != 0)
            {
                var hash = CryptoJS.HmacSHA1(pubKey + timestamp, privKey);
                return hash.toString();
            } else {
                return false;
            }
        } else
        {
            return false;
        }
    };

    var getHMACTest = function(username, timestamp, privKey) {
        var hash = CryptoJS.HmacSHA1(username + timestamp, privKey);
        return hash.toString();
    };

    var getMicrotime = function(get_as_float) {
        var now = new Date().getTime() / 1000;
        var s = parseInt(now, 10);
        return (get_as_float) ? now : (Math.round((now - s) * 1000) / 1000) + ' ' + s;
    };

    return {
        init: init
    };
})(jQuery);

function getCookie(name) {
    var dc = document.cookie;
    var prefix = name + "=";
    var begin = dc.indexOf("; " + prefix);
    if (begin == -1) {
        begin = dc.indexOf(prefix);
        if (begin != 0)
            return null;
    }
    else
    {
        begin += 2;
        var end = document.cookie.indexOf(";", begin);
        if (end == -1) {
            end = dc.length;
        }
    }
    return unescape(dc.substring(begin + prefix.length, end));
}

app.init();