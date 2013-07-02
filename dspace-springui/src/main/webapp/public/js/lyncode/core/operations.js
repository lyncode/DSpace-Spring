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
				contentType: "application/json",
				url: buildURL(url),
				cache: false
				
			})
			.done(successCallback)
			.error(errorCallback);
		},
		
		post: function (url, data, successCallback, errorCallback) {
			$.ajax({
				type: 'POST',
				contentType: "application/json",
				data: JSON.stringify(data),
				url: buildURL(url),
				cache: false
			})
			.done(successCallback)
			.error(errorCallback);
		}
	};
});