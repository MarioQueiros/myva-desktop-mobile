/*jslint laxbreak: true, strict: true*/
/*global localStorage, chrome*/

chrome.browserAction.onClicked.addListener(function () {
    chrome.windows.getCurrent(function (window) {
        chrome.windows.create({
            url: chrome.extension.getURL("index.html")
			, width: 500
			, height: 500
			, left: window.left + (window.width/2) - 250
			, top: window.top + (window.height/2) - 250
			, focused: true
			, type: "popup"
        })
    });
});
