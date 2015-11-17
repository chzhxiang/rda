/* Chinese initialisation for the jQuery UI date picker plugin. */
/* Written by TerryLee (binlic2010@gmail.com). */
define(function(require) { return function($) {
    require('./jquery.ui.datepicker.js')($);
	$.datepicker.regional['zh-CN'] = {
			closeText: '关闭',
			prevText: '上月',
			nextText: '下月',
			currentText: '今天',
			monthNames: ['一月','二月','三月','四月','五月','六月',
			'七月','八月','九月','十月','十一月','十二月'],
			monthNamesShort: ['一月','二月','三月','四月','五月','六月',
			          		'七月','八月','九月','十月','十一月','十二月'],
			dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
			dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
			dayNamesMin: ['日','一','二','三','四','五','六'],
			weekHeader: '周',
			firstDay: 1,
			isRTL: false,
			buttonImageOnly: true,
			showMonthAfterYear: true, // 月在年之后显示
			changeMonth:true,// 允许选择月份   
			changeYear:true,// 允许选择年份   
			dateFormat:'yy-mm-dd', // 设置日期格式   
			//showOn: 'both', // 在输入框旁边显示按钮触发，默认为：focus。还可以设置为both   
			//buttonImage: contextPath + '/common/images/calendar.gif',
			//buttonImageOnly: true, // 不把图标显示在按钮上，即去掉按钮   
			showOtherMonths: true,
			readOnly : true ,
			yearRange: 'c-70:c+10',
			yearSuffix: '年',
			showButtonPanel:true
		};
	$.datepicker.setDefaults($.datepicker.regional['zh-CN']);
}});
