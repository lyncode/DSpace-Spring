define([
        'jquery',
        'external/jquery.noty'
], function ($, Noty) {
	var Dialog = {
		error: function (message) {
			Noty({
				text: message,
				type: 'error',
				closeWith: ['click', 'hover']
			});
		},
		success: function (message) {
			Noty({
				text: message,
				type: 'success',
				closeWith: ['click', 'hover']
			});
		},
		question: function (message, yesFunction, noFunction) {
			Noty({
				text: message,
				type: 'alert',
				buttons: [
					{
						addClass: 'btn btn-success', 
						text: 'Yes', 
						onClick: function($noty) {
							$noty.close();
							if (yesFunction)
								yesFunction.apply({}, []);
						}
					},
					{
						addClass: 'btn btn-inverse', 
						text: 'No', 
						onClick: function($noty) {
							$noty.close();
							if (noFunction)
								noFunction.apply({},[]);
						}
					}          
				]
			});
		}
	};
	return Dialog;
});