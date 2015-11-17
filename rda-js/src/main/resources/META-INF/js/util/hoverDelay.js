define(function(require) { return function($) {
    $.fn.hoverDelay = function(options){
		var defaults = {
            hoverDuring: 200,
            outDuring: 200,
            hoverEvent: function(){
                $.noop();
            },
            outEvent: function(){
                $.noop();
            }
        };
        var sets = $.extend(defaults,options || {});
		var hoverTimer, outTimer;
		return $(this).each(function(){
			$(this).hover(function(){
				var t = this;
				clearTimeout(outTimer);
				hoverTimer = setTimeout(function (){
					sets.hoverEvent.call(t);
				}, sets.hoverDuring);
			},function(){
				var t = this;
				clearTimeout(hoverTimer);
				outTimer = setTimeout(function (){
					sets.outEvent.call(t);
				}, sets.outDuring);
			});
		});
	}
}});