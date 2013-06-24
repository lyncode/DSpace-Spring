define([
    'jquery',
    'lyncode/core/dialogs'    
], function ($, Dialog) {
	var buildURL = function (url) {
		return "/rest/" + url;
	};
	
	return {
		get: function (url, successCallback, errorCallback) {
			$.ajax({
				type: 'GET',
				url: buildURL(url),
				cache: false
				
			})
			.done(successCallback)
			.error(errorCallback);
		},
		
		post: function (url, data, successCallback, errorCallback) {
			$.ajax({
				type: 'POST',
				data: data,
				url: buildURL(url),
				cache: false
			})
			.done(successCallback)
			.error(errorCallback);
		}
	};
});