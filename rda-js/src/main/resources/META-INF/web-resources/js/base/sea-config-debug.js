seajs.config({

	// 别名配置
	alias : {
		'jquery' 		: '../jquery/jquery-1.10.2.js',
		'jquery.ui' 	: '../jquery-ui/1.10.3/jquery-ui.js'	//包含所有jquery ui 效果
	},
//	preload : [ 'jquery' ],
	debug : true,
	// map: [
	// [/^(.*\/resources\/.*\.js)(?:.*)$/i, function(url) {
	// return url.indexOf('-debug.js')===-1 ? url.replace('.js','-debug.js') :
	// url;
	// }]
	// ],
	plugins : [ 'nocache' ]
});