// Create the recognition object and define four event handlers (onstart, onerror, onend, onresult)
var recognition = new webkitSpeechRecognition();
//
var final_transcript = '';
var undo = '';
var redo = '';
var recognizing = false;

function loadVoice() {

    var language = 'en-GB';

    recognition.continuous = true; 
    recognition.interimResults = true; 
    recognition.lang = language;
    recognition.maxResults = 5;

    // Start
    recognition.onstart = function () {
        $("#voice-recognition-title").css('color', '#cc181e');
        recognizing = true;

        var t = $("#new-title");
        $(t).blur();
        $(t).focus();

        $("#new-title").selectLast();
    };

    // Error
    recognition.onerror = function (event) {
        $("#new-title").removeAttr('readonly');
    };


    /*chrome.tts.speak('Bravo',
        {
            rate: 0.5,
            volume: 1,
            pitch: 0,
            lang: 'en-GB'
        });

    };*/

    // Stop
    recognition.onend = function () {
        $("#voice-recognition-title").css('color', '#333333');
        recognizing = false;
    };

    // Result
    recognition.onresult = function (event) {
        var interim_transcript = '';

        // Assemble the transcript from the array of results
        for (var i = event.resultIndex; i < event.results.length; ++i) {

            if (event.results[i].isFinal) {

                // Keywords
                var keyword = event.results[i][0].transcript;
                keyword = keyword.replace(/\s/g, '');

                if (keyword == 'voiceeraseall') {
                    // Erase all
                    final_transcript = '';
                } else if (keyword == 'voicereturn') {
                    // Undo
                    redo = final_transcript;
                    final_transcript = undo;
                } else if (keyword == 'voicegoforward') {
                    // Redo
                    final_transcript = redo;
                } else if (keyword == 'voicehelp') { 
                    alert("Voice commands:\n\n - voice erase all\n - voice return\n - voice go forward")
                }
                else {
                    // Normal
                    undo = final_transcript;
                    final_transcript += event.results[i][0].transcript;
                }

                if (final_transcript.length > 0) {
                    final_transcript = capitaliseFirstLetter(final_transcript);
                }
                $("#new-title").val(final_transcript);

                var t = $("#new-title");
                $(t).blur();
                $(t).focus();

                $("#new-title").selectLast();

            } else {
                interim_transcript += event.results[i][0].transcript;
            }
        }
    };
}


function voiceStart() {
    // check that your browser supports the API
    if (!('webkitSpeechRecognition' in window)) {
        alert("Your Browser does not support the Speech API");

    } else {

        if (recognizing) {
            $("#new-title").removeAttr('readonly');

            recognition.stop();
            recognizing = false;
        } else {

            $("#new-title").attr('readonly', 'readonly');

            final_transcript = $("#new-title").val() + " ";

            // Request access to the User's microphone and Start recognizing voice input
            recognition.start();
        }
    }
};