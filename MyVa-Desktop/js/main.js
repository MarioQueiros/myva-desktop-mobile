// Dates
var dayarray = new Array("Sunday", "Monday",
 "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

var montharray = new Array("January", "February", "March",
 "April", "May", "June", "July", "August", "September",
 "October", "November", "December")

function todayDate() {
    var mydate = new Date()
    var year = mydate.getYear()
    if (year < 1000)
        year += 1900
    var day = mydate.getDay()
    var month = mydate.getMonth()
    var daym = mydate.getDate()
    if (daym < 10)
        daym = "0" + daym
    var hours = mydate.getHours()
    var minutes = mydate.getMinutes()
    var seconds = mydate.getSeconds()
    var dn = "AM"
    if (hours >= 12)
        dn = "PM"
    if (hours > 12) {
        hours = hours - 12
    }
    if (hours == 0)
        hours = 12
    if (minutes <= 9)
        minutes = "0" + minutes
    if (seconds <= 9)
        seconds = "0" + seconds
    //change font size here
    var cdate = dayarray[day] + ", " + montharray[month] + " " + daym + ", " + year;
    document.getElementById("today-date-text").innerHTML = cdate;
    document.getElementById("last-sync-text").innerHTML = "Last sync - "+cdate;
}

//
var reconf = "";

// Setting menu function
//
//
$("#settings-button").click(
function () {
    $("#event-list-menu").css('bottom', '0px');
    $("#event-manager-div").fadeOut(250);

    if (document.getElementById("profile-settings-menu").style.display != "none") {
        $("#profile-settings-menu").fadeOut(500);
    } else {
        $("#profile-settings-menu").fadeIn(500);
    }
});


// Go to add new event function
//
//
$("#adding-button").on('click', function (e) {
    e.preventDefault();

    if (getCookie("app_state") == "view_event") {
        returnFromViewEvent(250);
    } else {
        var now = new Date();
        var month = (now.getMonth() + 1);
        var day = now.getDate();
        if (month < 10)
            month = "0" + month;
        if (day < 10)
            day = "0" + day;
        var today = now.getFullYear() + '-' + month + '-' + day;

        $('#new-date').val(today);
        $('#new-place').val("");
        $('#new-title').val("");

        gotoCreateEvent(250);
    }
});
//
function gotoCreateEvent(time) {

    if (getCookie("app_state") == "view_event") {
        returnFromViewEvent(time);
    }

    $("#event-list-menu").css('bottom', '0px');
    $("#event-manager-div").fadeOut(time);
    $("#profile-settings-menu").fadeOut(time);

    $("#adding-button").fadeOut(time * 2);
    $("#event-bar-date").fadeOut(time, function () {
        $("#new-event-buttons").fadeIn(time);
    });

    $("#new-event-menu").animate({ left: "-100%" }, 0, function () {
        $("#new-event-menu").fadeIn(0);
        $("#new-event-menu").animate({ left: "0%" }, time * 2);
        $("#event-list-menu").animate({ left: "100%" }, time * 2);
    });

    createCookie("app_state", "create_event", 0);
}


// Go to add new event function
//
//
$("#view-event-menu").on('click', function (e) {
    e.preventDefault();

    gotoViewEvent(250);
});
//
function gotoViewEvent(time) {

    $("#event-list-menu").css('bottom', '0px');
    $("#event-manager-div").fadeOut(time);

    $("#profile-settings-menu").fadeOut(time);

    $("#event-name").text(selectedName);
    $("#event-place").text(selectedPlace);
    $("#event-date").text(selectedDate);

    $("#event-menu").animate({ right: "-100%" }, 0, function () {
        $("#event-menu").fadeIn(0);
        $("#event-menu").animate({ right: "0%" }, time * 2);
        $("#event-list-menu").animate({ right: "100%" }, time * 2);
    });

    $("#adding-button").fadeOut(time, function () {
        $("#adding-button").removeClass("fa-plus");
        $("#adding-button").addClass("fa-reply");

        $("#adding-button").fadeIn(time);
    });

    createCookie("app_state", "view_event", 0);

    google.maps.event.trigger(map2, 'resize');
    centerMap2();

    if (selectedLat != "" && selectedLng != ""){

        var myLatlng = new google.maps.LatLng(selectedLat, selectedLng);
        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map2,
            draggable: false,
            animation: google.maps.Animation.BOUNCE,
            title: selectedPlace
        });

        map2.setCenter(myLatlng);
    }

}
//
function returnFromViewEvent(time) {
    $("#event-list-menu").animate({ right: "0%" }, time * 2);
    $("#event-menu").animate({ right: "-100%" }, time * 2);
    $("#event-menu").fadeOut(0);
    $("#event-menu").animate({ right: "0%" }, 0);

    $("#adding-button").fadeOut(time, function () {
        $("#adding-button").removeClass("fa-reply");
        $("#adding-button").addClass("fa-plus");

        $("#adding-button").fadeIn(time);
    });

    createCookie("app_state", "events_list", 0);
}

// Loading screen
//
//
function turnOnSpinner(text, callback) {
    $("#spinnerText").text(text);
    $("#spinner-div").fadeIn(100);
    callback();
}
//
function turnOfSpinner(error, text, callback) {
    $("#spinnerText").text(text);
    if (error) {
        $(".spinner").addClass("spinner-error");
        $("#spinnerText").addClass("spinner-error");

        $("#spinner-div").fadeOut(700, function () {
            $(".spinner").removeClass("spinner-error");
            $("#spinnerText").removeClass("spinner-error");
            $("#spinnerText").text("Loading");
        });
    } else {
        $("#spinner-div").fadeOut(700, function () {
            $("#spinnerText").text("Loading");
        });
    }
    callback();
}

// Syncronize Manual
//
//
$("#syncronization-button").on('click', function (e) {
    e.preventDefault();
    $("#settings-button").trigger('click');

    turnOnSpinner("Syncronizing", function () { });
    syncronizeFromServer(getCookie('username'), getCookie('pub_key'), function (type) {
        if (type) {
            todayDate();
            turnOfSpinner(false, "Syncronize completed", function () { });
        } else {
            turnOfSpinner(true, "Error while syncronizing", function () { });
        }
    });
});

// Cancel New Event creation
//
//
$("#cancel-new-event").on('click', function (e) {
    e.preventDefault();

    var state = getCookie("app_state");

    if (state == "create_event"){
        leaveCreateEvent(250);
    } else if (state == "select_place") {
        returnFromPlacesMenu(250);
    }
});
//
function leaveCreateEvent(time) {
    $("#adding-button").fadeIn(time * 2);
    $("#new-event-buttons").fadeOut(time, function () {
        $("#event-bar-date").fadeIn(time);
    });

    $("#event-list-menu").animate({ left: "0%" }, time * 2);
    $("#new-event-menu").animate({ left: "-100%" }, time * 2);
    $("#new-event-menu").fadeOut(0);
    $("#new-event-menu").animate({ left: "0%" }, 0);


    createCookie("app_state", "events_list", 0);
}


// Login function
//
//
//
$("#login-button").on('click', function (e) {
    e.preventDefault();

    var u = $('#login-username').val();
    var p = $('#login-password').val();
    var auth = make_base_auth(u, p);

    var state = getCookie("app_state");
    if (state == "confirm_account") {
        var c = u;
        u = getCookie("username");

        turnOnSpinner("Confirming account", function () { });
        confirm(u, c, function (type) {
            if (type) {
                turnOfSpinner(false, "Welcome " + capitaliseFirstLetter(u) + "!", function () {
                    loginAnimation(250, u);
                    $("#login-password-div").fadeIn(0);
                    $($("#login-user-div").children().eq(0)).text("Username");
                    $("#login-username").val("");
                    $("#login-password").val("");
                });
            } else {
                // Confirm error
                turnOfSpinner(true, "Wrong code", function () { });
            }
        });
    }
    else {
        turnOnSpinner("Login in", function () { });
        login(u, p, auth, function (usr, type) {
            if (type) {
                if (usr != "") {
                    turnOfSpinner(false,"Welcome " + capitaliseFirstLetter(u) + "!", function () {
                        loginAnimation(250, u);
                    });
                } else {
                    reconf = "";
                    gotoConfirmAccount(250, u);
                    $("#spinnerText").text("Sending confirmation email");
                }
            }else {
                if (usr != "") {
                    reconf = "reconfirmation";
                    gotoConfirmAccount(250, u);
                    $("#spinnerText").text("Sending confirmation email");
                } else {
                    // Error
                    turnOfSpinner(true, "Error login in", function () { });
                }
            }
        });
    }
});
//
function loginAnimation(time, user) {
    // Default - 500
    $("#initial-menu").fadeOut(time);
    $("#middle-bar").fadeIn(time);
    $("#event-list-menu").fadeIn(time);
    $("#top-buttons").fadeIn(time);

    // Saves session
    createCookie("username", user, 0);
    createCookie("app_state", "events_list", 0);

    $("#profile_name").text(capitaliseFirstLetter(user));
}


// Logout function
//
//
//
$("#logout-button").on('click', function (e) {
    e.preventDefault();

    if (getCookie("app_state") == "select_place") {
        // A little more quicker than the original
        returnFromPlacesMenu(25);
        leaveCreateEvent(25);
    }
    else if (getCookie("app_state") == "create_event"){
        // A little more quicker than the original
        leaveCreateEvent(50);
    }
    else if (getCookie("app_state") == "view_event") {
        returnFromViewEvent(50);
    }
    logoutAnimation();
});
//
function logoutAnimation() {
    $("#profile-settings-menu").fadeOut(250, function () {
        $("#middle-bar").fadeOut(500);
        $("#event-list-menu").fadeOut(500);
        $("#top-buttons").fadeOut(500);
        $("#initial-menu").fadeIn(500);
    });

    createCookie("username", "", -1);
    createCookie("app_state", "", -1);
    createCookie("pub_key", "", -1);
}

// Go to confirm account
//
//
function gotoConfirmAccount(time, user) {
    createCookie("username", user, 0);

    resendConfirm(user, function (type) {
        if (type) {
            // Success retrieval
            $("#login-password-div").fadeOut(time);
            $("#login-user-div").fadeOut(time, function () {
                $($("#login-user-div").children().eq(0)).text("Confirmation code");
                $("#login-username").val("");

                $("#login-user-div").fadeIn(time);
                turnOfSpinner(false, "Email sent", function () {});
            });

            createCookie("app_state", "confirm_account", 0);
        } else {
            // Error resend
            turnOfSpinner(true, "Error sending email, try again later", function () { });
        }
    });
}

// Go to register menu function
//
//
$("#register-button").on('click', function (e) {
    e.preventDefault();

    gotoRegisterMenu(500);
});
//
function gotoRegisterMenu(time) {
    $("#login-menu").fadeOut(time);
    $("#register-menu").fadeIn(time);
}

// Finish a new regist
//
//
$("#conclude-register-button").on('click', function (e) {
    e.preventDefault();
    var u = $('#register-username').val();
    var p = $('#register-password').val();
    var e = $('#register-email').val();
    var b = $('#register-birthday').val();

    var treatedDate = b.split("-");
    if (treatedDate.length > 0) {
        b = treatedDate[1].toString() + "/" + treatedDate[2].toString() + "/" + treatedDate[0].toString();
    }

    turnOnSpinner("Creating account", function () { });
    register(u, p, e, b, function (type) {
        if (type) {
            turnOfSpinner(false, "Account created", function () {
                returnFromRegisterMenu(500);
            });
        } else {
            // Register error
            turnOfSpinner(true, "Error while creating", function () { });
        }
    });
});


// Go from register menu to login menu function
//
//
$("#unregister-button").on('click', function (e) {
    e.preventDefault();

    returnFromRegisterMenu(500);
});
//
function returnFromRegisterMenu(time) {
    $("#register-menu").fadeOut(time);
    $("#login-menu").fadeIn(time);
}


// Go to Forgot password menu
//
//
//$("#alert-span").text("A recovery e-mail was sent.");
$("#forgot-password-button").on('click', function (e) {
    e.preventDefault();
    $("#login-menu").fadeOut(500);
    $("#forgot-password-menu").fadeIn(500);
});


// Go from forgot password menu to login menu function
//
//
$("#unrecover-button").on('click', function (e) {
    e.preventDefault();
    $("#forgot-password-menu").fadeOut(500);
    $("#login-menu").fadeIn(500);
});


// Blurring of recovering text
//
//
$("#forgot-username").focus(function () {
    $("#forgot-email").addClass("blurred-black-text");
    $("#forgot-username").removeClass("blurred-black-text");
});
//
$("#forgot-email").focus(function () {
    $("#forgot-username").addClass("blurred-black-text");
    $("#forgot-email").removeClass("blurred-black-text");
});

// Create new event
//
//
$("#confirm-new-event").on('click', function (e) {
    e.preventDefault();
    var state = getCookie("app_state");

    if (state == "create_event") {
        // AddEvent
        var u = $('#new-title').val();
        var p = $('#new-place').val();
        var e = $('#new-date').val();

        var timestamp = getMicrotime(true).toString();

        var treatedDate = e.split("-");
        if (treatedDate.length > 0) {
            e = treatedDate[1].toString() + "/" + treatedDate[2].toString() + "/" + treatedDate[0].toString();
        }
        turnOnSpinner("Saving event", function () { });

        var newIsFine = true;
        var oldIsFine = true;

        if (p != "" && u != "") {
            if (newLocal) {
                if (newMarkerLatitude != "" && newMarkerLongitude != "" && newMarkerName != "") {
                    newIsFine = true;
                } else {
                    newIsFine = false;
                }
            } else {
                if (availableLocals[selectedIndex] != p) {
                    oldIsFine = false;
                } else {
                    oldIsFine = true;
                }
            }

            if(newIsFine && oldIsFine){
                addEvent(u, p, e, function (type) {
                    if (type) {
                        turnOfSpinner(false,"Event saved", function () { });
                    }else{
                        turnOfSpinner(true,"Error while saving event", function () { });
                    }
                });
            } else {
                turnOfSpinner(true, "Error in event place", function () { });
            }
        } else {
            turnOfSpinner(true, "Error, found empty fields", function () { });
        }
        
    } else if (state == "select_place") {
        // Add place
        returnFromPlacesMenu(250);

        if (newLocal) {
            $("#new-place").val(newMarkerName);
        } else {

            selectedIndex = $("#input-place-name").prop("selectedIndex") - 1;
            if (selectedIndex != -1) {
                $("#new-place").val(availableLocals[selectedIndex]);
            }
        }
    }
});

// Voice recognition in create new event
//
//
$("#voice-recognition-title").on('click', function (e) {
    e.preventDefault();

    voiceStart();
});

// Goto Places menu
$("#places-icon").on('click', function (e) {
    e.preventDefault();

    gotoPlacesMenu(500);
});
//
function gotoPlacesMenu(time) {
    $("#new-event-start-title").fadeOut(time);
    $("#new-title-div").fadeOut(time);
    $("#new-place-div").fadeOut(time);
    $("#new-date-div").fadeOut(time, function () {
        document.getElementById('new-event-start-title').innerHTML = "Select place";
        $("#new-event-start-title").fadeIn(time);

        // Loads the public settings
        $("#place-type").fadeIn(time);
        $("#place-name").fadeIn(time);
        $("#place-maps").fadeIn(time);

        google.maps.event.trigger(map, 'resize');
        centerMap();
    });

    createCookie("app_state", "select_place", 0);
}
//
function returnFromPlacesMenu(time) {
    $("#new-event-start-title").fadeOut(time);
    $("#place-type").fadeOut(time);
    $("#place-name").fadeOut(time);
    $("#place-maps").fadeOut(time, function () {
        document.getElementById('new-event-start-title').innerHTML = "Add new event";
        $("#new-event-start-title").fadeIn(time);
        $("#new-title-div").fadeIn(time);
        $("#new-place-div").fadeIn(time);
        $("#new-date-div").fadeIn(time);
    });

    createCookie("app_state", "create_event", 0);
}

// Create a event div
//
// 
var selectedEventDiv = null;
var selectedEventIndex = -1;
var selectedName = "";
var selectedPlace = "";
var selectedDate = "";
var selectedLat= "";
var selectedLng = "";
//
function createEventDiv(n, pl, t, d, id, index, localID) {
    var divPrinc = document.createElement('div');
    divPrinc.setAttribute('class', 'event-item'); 
    var divClear = document.createElement('div');
    divClear.setAttribute('class', 'clearfix');

    var left = document.createElement('aside');
    left.setAttribute('class', 'left');
    var right = document.createElement('aside');
    right.setAttribute('class', 'right');

    var space = document.createElement('br');

    var name = document.createElement('span');
    name.innerHTML = n;
    var place = document.createElement('span');
    place.innerHTML = pl;
    var today = document.createElement('span');
    if(t)
        today.innerHTML = "Today";
    var date = document.createElement('span');
    date.innerHTML = d;

    //var img = document.createElement('img');
    //img.setAttribute('src', 'img/nexus 7 banner.png');

    left.appendChild(name);
    left.appendChild(space);
    left.appendChild(place);

    space = document.createElement('br');

    right.appendChild(today);
    right.appendChild(space);
    right.appendChild(date);
    
    divPrinc.appendChild(left);
    divPrinc.appendChild(right);
    divPrinc.appendChild(divClear);

    divPrinc.onclick = function () {
        if (selectedEventDiv != null) {
            $(selectedEventDiv).css('background-color', '#EEEEEE');
        }

        $(this).css('background-color', '#B8C2D0');
        $("#profile-settings-menu").fadeOut(250);

        if (selectedEventDiv == this) {
            $("#event-list-menu").css('bottom', '0px');
            $("#event-manager-div").fadeOut(250);
            $(selectedEventDiv).css('background-color', '#EEEEEE');
            selectedEventDiv = null;
            selectedEventIndex = -1;
            selectedName = "";
            selectedPlace = "";
            selectedDate = "";
            $("#profile-settings-menu").fadeOut(250);
        } else {
            $("#event-list-menu").css('bottom', '60px');
            $("#event-manager-div").fadeIn(250);
            selectedEventDiv = this;
            selectedEventIndex = id;
            selectedName = n;
            selectedPlace = pl;
            selectedDate = d;

            var index = arraySearch(availableLocalsIds, localID);
            if (index >= 0) {
                selectedLat = availableLocalsCords[index].split(';')[0];
                selectedLng = availableLocalsCords[index].split(';')[1];
            }

            chrome.tts.speak(n,
                {
                    rate: 0.5,
                    volume: 1,
                    pitch: 0,
                    lang: 'en-GB'
                });
        }
    };
    
    document.getElementById('event-list-menu').appendChild(divPrinc);
}
//
$("#delete-event-button").on('click', function (e) {
    if (selectedEventIndex != -1){
        removeEvent(selectedEventIndex, function (type) {
        })
    }
});

// Clear events
//
//
function clearEvents() {
    var arrayChilds = document.getElementById('event-list-menu').children;

    while (arrayChilds.length > 2) {
        document.getElementById('event-list-menu').removeChild(document.getElementById('event-list-menu').lastChild);
    }
}

// Show no events menu
//
//
function showNoEvents(type) {
    if (type) {
        $('#warning-no-events-text').removeClass('hidden');
    } else {
        $('#warning-no-events-text').addClass('hidden');
    }
}

/* ==========================================================================
   Voice
   ========================================================================== */

// Voice Synthesis
//
// 
/*

*/
//

/* ==========================================================================
   Images
   ========================================================================== */

// Canvas
//
//
$("new-image-canvas");

/* ==========================================================================
   Maps and places
   ========================================================================== */

var map
var map2;
var markers = [];
var drawingManager;
var geocode;
//
var newMarker;
var newMarkerLatitude;
var newMarkerLongitude;
var newMarkerName;
//
var currentLocation;

// New event Places
var newLocal = true;
var selectedIndex = -1;
//
$("#new-event-menu").on('scroll', function () {
    $("input").blur();
});
//


// Maps
//
//
function initializeMaps() {
    geocoder = new google.maps.Geocoder();
    //
    var mapOptions = {
        zoom: 13,
        scrollwheel: false
    };
    map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
    map2 = new google.maps.Map(document.getElementById("map-canvas_2"), mapOptions);

    // Create new places
    drawingManager = new google.maps.drawing.DrawingManager({
        drawingControl: false,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: [
              google.maps.drawing.OverlayType.MARKER
            ]
        },
        markerOptions: {
            draggable: true,
            animation: google.maps.Animation.DROP
        }
    });

    // Remove when created one place
    google.maps.event.addListener(drawingManager, 'markercomplete', function (marker) {
        drawingManager.setOptions({
            drawingControl: false
        });

        if (newMarker) {
            newMarker.setMap(null);
            google.maps.event.clearInstanceListeners(newMarker);
        }

        newMarker = marker;

        if (newMarker.position != null){
            newMarkerLatitude = parseFloat(newMarker.position.lat());
            newMarkerLongitude = parseFloat(newMarker.position.lng());
            var latlng = new google.maps.LatLng(newMarkerLatitude, newMarkerLongitude);


            geocoder.geocode({ 'latLng': latlng}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {

                    var placeName = "";
                    var streetFound = false;
                    for (var i = 0; i < results.length; i++) {

                        if (results[i].types.indexOf('street_address') > -1) {
                            placeName += results[i].formatted_address.split(',')[0];
                            streetFound = true;
                        }

                        if (results[i].types.indexOf('route') > -1) {
                            if (!streetFound){
                                placeName += results[i].formatted_address.split(',')[0];
                                streetFound = true;
                            }
                        }


                        if (results[i].types.indexOf('administrative_area_level_3') > -1 || results[i].types.indexOf('administrative_area_level_2') > -1 ||
                            results[i].types.indexOf('administrative_area_level_1') > -1) {

                            if (streetFound) {
                                placeName += ", ";
                            }

                            placeName += results[i].formatted_address.split(',')[0];

                            break;
                        }

                    }

                    newMarkerName = placeName;

                    var elem = document.getElementById("input-place-name");
                    $(elem.options[0]).text(placeName);
                }
            });
        }
    });

    drawingManager.setMap(map);
    drawingManager.setDrawingMode(google.maps.drawing.OverlayType.MARKER);

}
//
// Function for handling gps usage denial
function handleNoGeolocation() {
    
        var options = {
            map: map,
            position: new google.maps.LatLng(60, 105),
            zoom:1
        };

        var options1 = {
            map: map2,
            position: new google.maps.LatLng(60, 105),
            zoom: 1
        };

        map.setCenter(options.position);
        map2.setCenter(options1.position);
}
//
// Function for bounce marker
function toggleBounce(marker) {

    if (marker.getAnimation() != null) {
        marker.setAnimation(null);

        $('#input-place-name option:eq(0)').prop('selected', true);
    } else {
        for (var j = 0; j < markers.length; j++) {
            markers[j].setAnimation(null);
        }

        marker.setAnimation(google.maps.Animation.BOUNCE);
        var len = $('#input-place-name option').size();

        for (var k = 0; k < len ; k++) {
            if (marker.getTitle() == $("#input-place-name option:eq(" + k + ")").text()) {
                $('#input-place-name option:eq(' + k + ')').prop('selected', true);
            }
        }
    }
}
//
$('#input-place-name').on('change', function () {
    for (var k = 0; k < markers.length ; k++) {
        if (markers[k].getTitle() == $("#input-place-name option:selected").text()) {
            toggleBounce(markers[k]);
        }
    }
})
// Function for adding markers in the map
function initializeMarkers() {

    markers = [];

    for (var i = 0; i < availableLocalsCords.length; i++) {

        var latitude = availableLocalsCords[i].split(';')[0];
        var longitude = availableLocalsCords[i].split(';')[1];

        var myLatlng = new google.maps.LatLng(latitude, longitude);
        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map,
            draggable: false,
            animation: google.maps.Animation.DROP,
            title: availableLocals[i]
        });

        google.maps.event.addListener(marker, 'click', function () {
            selectedIndex = -1;
            toggleBounce(this);
        });

        markers.push(marker);
    }
}
//
function removeMarkers() {
    for (var i = 0; i < markers.length; i++) {
        removeMarkerWithAnimation(markers[i]);
    }
}
//
$("#r1").on('click', createNewEvent);
$("#r2").on('click', useExistingEvent);
//
// Add marker on click in map
//
function createNewEvent() {
    $("#r1").attr('disabled', 'disabled');

    $("#spe-1").fadeOut(250, function () {

        $("#spe-1").text("Tip: Click on a place in the map");
        $("#spe-1").fadeIn(250, function () {
            $("#r2").removeAttr('disabled');
        });
    });

    $("#input-place-name").val("");
    $("#input-place-name").attr('disabled', 'disabled');

    if (drawingManager != null)
        drawingManager.setDrawingMode(google.maps.drawing.OverlayType.MARKER);

    newLocal = true;

    removeMarkers();
}
//
function useExistingEvent() {
    $("#r2").attr('disabled', 'disabled');

    $("#spe-1").fadeOut(250, function () {

        $("#spe-1").text("Tip: Search by name or in the map");
        $("#spe-1").fadeIn(250, function () {
            $("#r1").removeAttr('disabled');
        });
    });

    if (drawingManager != null)
        drawingManager.setDrawingMode(google.maps.drawing.OverlayType.HAND);

    if (newMarker) {
            removeMarkerWithAnimation(newMarker);
    }
    newMarker = null;
    var elem = document.getElementById("input-place-name");
    $(elem.options[0]).text("");

    newLocal = false;

    $("#input-place-name").removeAttr('disabled');

    initializeMarkers();

    $("#input-place-name").val("");
}
//
$("#input-place-name").on('input', function (e) {
    e = e || window.event;

    for (var o = 0; o < markers.length; o++) {
        if (markers[o].getTitle() == $("#input-place-name").val()) {
            toggleBounce(markers[o]);
            selectedIndex = o;
        } else {
            markers[o].setAnimation(null);
        }
    }
});
//
$("#input-place-name").on('keypress', function (e) {
    e = e || window.event;

    for (var o = 0; o < markers.length; o++) {
        if (markers[o].getTitle() == $("#input-place-name").val()) {
            toggleBounce(markers[o]);
            selectedIndex = o;
        } else {
            markers[o].setAnimation(null);
        }
    }
});


function removeMarkerWithAnimation(marker) {
    (function animationStep() {
            //Converting GPS to World Coordinates
            var newPosition = map.getProjection().fromLatLngToPoint(marker.getPosition());

            //Moving 10px to up
            newPosition.y -= 10 / (1 << map.getZoom());

            //Converting World Coordinates to GPS 
            newPosition = map.getProjection().fromPointToLatLng(newPosition);
            //updating maker's position
            marker.setPosition(newPosition);
            //Checking whether marker is out of bounds
            if (map.getBounds().getNorthEast().lat() < newPosition.lat()) {
                marker.setMap(null);
            } else {
                //Repeating animation step
                setTimeout(animationStep, 10);
            }
    })();
}
//
function centerMap() {
    navigator.geolocation.getCurrentPosition(
        function (position) {

            var pos = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

            var yourlocation = new google.maps.Marker({
                map: map,
                draggable: false,
                position: pos,
                icon: {
                    path: google.maps.SymbolPath.CIRCLE,
                    scale: 5,
                    strokeWeight: 2,
                    strokeColor: "#AAAAAA",
                    fillColor: "#133361",
                    fillOpacity: 1
                }
            });
            currentLocation = yourlocation;

            map.setCenter(pos);

            // Tooltip current location
            var contentString = '<div id="content">' +
              '<p>Your actual location</p>'
            '</div>';

            var infoWindow = new google.maps.InfoWindow({
                content: contentString
            });
            google.maps.event.addListener(currentLocation, 'click', function () {
                if (currentLocation != null) {
                    infoWindow.open(map, currentLocation);
                }
            });
        }, function () {
            // Case user rejects
            handleNoGeolocation();
        });
}
//
function centerMap2() {
    navigator.geolocation.getCurrentPosition(
        function (position) {

            var pos = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

            var yourlocation = new google.maps.Marker({
                map: map2,
                draggable: false,
                position: pos,
                icon: {
                    path: google.maps.SymbolPath.CIRCLE,
                    scale: 5,
                    strokeWeight: 2,
                    strokeColor: "#AAAAAA",
                    fillColor: "#133361",
                    fillOpacity: 1
                }
            });
            currentLocation = yourlocation

            // Tooltip current location
            var contentString = '<div id="content">' +
              '<p>Your actual location</p>'
            '</div>';

            var infoWindow = new google.maps.InfoWindow({
                content: contentString
            });
            google.maps.event.addListener(currentLocation, 'click', function () {
                if (currentLocation != null) {
                    infoWindow.open(map2, currentLocation);
                }
            });
        }, function () {
            // Case user rejects
            handleNoGeolocation();
        });
}
//
function loadMapsScript() {
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyD-yGIn57VqyNMzMgIoYrmTUGRm5alkSGA&sensor=true&libraries=drawing&language=en-GB&' +
        'callback=initializeMaps';
    document.body.appendChild(script);
}


/* ==========================================================================
   Utils
   ========================================================================== */

// Capitalizer
function capitaliseFirstLetter(string) {

    if (string.charAt(0) == " ") {
        return string.charAt(1).toUpperCase() + string.slice(2);
    }else{
        return string.charAt(0).toUpperCase() + string.slice(1);
    }
}

// Selector
$.fn.selectRange = function (start, end) {
    return this.each(function () {
        if (this.setSelectionRange) {
            this.focus();
            this.setSelectionRange(start, end);
        } else if (this.createTextRange) {
            var range = this.createTextRange();
            range.collapse(true);
            range.moveEnd('character', end);
            range.moveStart('character', start);
            range.select();
        }
    });
};

// End Selector
$.fn.selectLast = function () {
    var l = this.val().length;

    return this.selectRange(l, l);
}

/* ==========================================================================
   Load
   ========================================================================== */

// Load function
//
//
window.onload = function () {
    $('#login-username').val(username);
    //
    todayDate();
    //
    loadVoice();
    //
    document.getElementById("r1").click();
    //
    loadMapsScript();

};