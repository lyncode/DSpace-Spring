define(['lyncode/core/config'], function (config) {
	return function (path) {
		if (path.match(/^\//))
			return config.baseUrl + path;
		else
			return config.baseUrl + "/" + path;
	};
});