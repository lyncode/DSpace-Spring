define([
    'jquery',
    'lyncode/operations/server',
    'lyncode/core/object'
], function ($, Class, ServerOperations) {
   return Class.extend({
        init: function (args) {
            if (args && args.length && args.length > 0)
                this.options = $.extend({
                    errorMessage: 'Something went wrong. Please refresh your browser.',
                    interval: 1000,
                    okCallback: function () {
                        // don't do anything
                    }
                }, args[0]);

            this.stop = false;
        },

        install: function () {
            var self = this;
            ServerOperations.install(function () {
                // Trigger the interval
                setTimeout(function () {
                    self.checkStatus();
                }, self.options.interval);
            }, self.options.errorMessage);
        },

        checkStatus: function () {
            var self = this;
            ServerOperations.getStatus(function () {
                self.options.okCallback.apply({}, []);
            }, function () {
                setTimeout(function () {
                    self.checkStatus();
                }, self.options.interval);
            });
        }
   });
});