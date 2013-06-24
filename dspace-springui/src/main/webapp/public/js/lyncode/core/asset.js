define(['lyncode/core/config'], function (config){
	return function (path) {
		if (path.match(/^\//))
			return config.baseUrl + "/public" + path;
		else
			return config.baseUrl + "/public/" + path;
	};
});