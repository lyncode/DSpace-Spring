require([
	'jquery',
	'lyncode/dao/harvest',
	'doT!templates/test'
	
], function($, harvestService, tmpl) {
	harvestService.getLastHarvest(4).done(function (data) {
		$(document).ready(function () {
			$('#id').html(tmpl(data));
		});
	});
});
