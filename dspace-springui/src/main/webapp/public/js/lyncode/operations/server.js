define([
    'lyncode/core/dialogs',
    'lyncode/core/operations'
], function (Dialog, Operation) {
    var buildURL = function (url) {
        return "server/" + url;
    };

    return {
        getStatus: function (successCallback, errorCallback) {
            Operation.get(
                buildURL("status"),
                function (data) { // Success callback
                    successCallback.apply({}, []);
                },
                function () { // Error callback
                    errorCallback.apply({}, []);
                }
            );
        },

        install: function (successCallback, errorMessage) {
            Operation.get(
                buildURL("install"),
                function (data) {
                    successCallback.apply({}, []);
                },
                function () {
                    errorMessage = errorMessage || "Something went wrong. Please refresh your browser.";
                    Dialog.error(errorMessage);
                }
            );
        }
    };
});