define(function(require) { return function($) {
	require('./jquery.ui.dialog.js')($);
	var jQuery = $,defaultTitle = "系统提示";
	  $.msg = {
				alert: function(title, content, fn ,options) {
					if(!content) {
						content = title;
						title = defaultTitle;
					}
					if($.isFunction(content)) {
						options = fn;
						fn = content;
						content = title;
						title = defaultTitle;
					}
					if(!$.isFunction(fn)) {
						options = fn;
						fn = null;
					}
					var msg;
					if(content instanceof jQuery) {
						msg = content;
					} else {
						msg = $('<div />').html(content.toString());
					}
					var defaults = {
							title: title,
							modal: true,
							resizable: false,
							closeOnEscape:false,
							buttons: {
								'确定': function() {
									$(this).dialog("close"); 
								}
							},
							close: function() {
								if(fn) fn();
							}
						};
					$.extend(defaults, options);
					msg.dialog(defaults);
				},
				confirm: function(title,content,fn,options) {
					if(!content) {
						content = title;
						title = defaultTitle;
					}
					if($.isFunction(content)) {
						fn = content;
						content = title;
						title = defaultTitle;
					}
					var msg;
					if(content instanceof jQuery) {
						msg = content;
					} else {
						msg = $('<div/>').html(content.toString());
					}
					var result;
					var a = {
							title: title,
							resizable: false,
							modal: true,
							closeOnEscape:false,
							buttons: {'确定': function() {
								result = true;						
								$(this).dialog("close"); 
							},'取消': function() {
								result = false;
								$(this).dialog("close");
							}
							},
							close: function() {
								if(fn) fn(result);
							}
						};
					$.extend(a, options);
					msg.dialog(a);
				}
			};
}});