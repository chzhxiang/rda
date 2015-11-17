define(function(require) { return function($) {
	require('./jquery.blockUI.js')($);
	var customizeOptions={};
	$.extend(customizeOptions, $.blockUI.defaults);
	customizeOptions.message="数据读取中,请稍等...";
	customizeOptions.css={
			margin:		0,
			width:		'30%',
			top:		'40%',
			left:		'35%',
			textAlign:	'center',
			cursor:		'wait',
			border: 'none', 
	        padding: '15px', 
	        backgroundColor: '#000', 
	        '-webkit-border-radius': '10px', 
	        '-moz-border-radius': '10px', 
	        opacity: .5, 
	        color: '#fff' 	
	};
	$.blockUI.defaults = customizeOptions;
}});