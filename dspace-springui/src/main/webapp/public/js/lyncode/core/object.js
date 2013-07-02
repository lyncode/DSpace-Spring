define([], function () {
    "use strict";

    var initializing = false;
    // The base Class implementation (does nothing)
    var Class = function () {
    };

    /**
     * Create a new Class that inherits from this class
     * @member Class
     * @param prop
     * @return {Function}
     */
    Class.extend = function (prop) {
        var _super = this.prototype;

        // Instantiate a base class (but only create the instance,
        // don't run the init constructor)
        initializing = true;
        var prototype = new this();
        initializing = false;

        // Copy the properties over onto the new prototype
        for (var name in prop) {
            // Check if we're overwriting an existing function
            prototype[name] = typeof prop[name] == "function" &&
                typeof _super[name] == "function" && fnTest.test(prop[name]) ?
                (function (name, fn) {
                    return function () {
                        var tmp = this._super;

                        // Add a new ._super() method that is the same method
                        // but on the super-class
                        this._super = _super[name];

                        // The method only need to be bound temporarily, so we
                        // remove it when we're done executing
                        var ret = fn.apply(this, arguments);
                        this._super = tmp;

                        return ret;
                    };
                })(name, prop[name]) :
                prop[name];
        }

        // The dummy class constructor
        function Class() {
            // All construction is actually done in the init method
            if (!initializing && this.init) {
                this.init.call(this, Array.prototype.slice.call(arguments));
            }
        }

        //add an instanceof method
        // To the best of my knowledge, this should work,
        // unless comparing objects created in diferent frames
        prototype.instanceOf = function(o){
            if (o && o.prototype) {
                return o.prototype.init === this.init;
            }else{
                return false;
            }

        };

        // Populate our constructed prototype object
        Class.prototype = prototype;

        // Enforce the constructor to be what we expect
        Class.prototype.constructor = Class;

        // keep same structure as in ember
        Class.create = function () {
            initializing = true;
            var instance = new this();
            initializing = false;
            if(instance.init)
                instance.init.apply(instance, Array.prototype.slice.call(arguments));
            return instance;
        };

        // And make this class extendable
        Class.extend = this.extend;

        return Class;
    };

    return Class;
});