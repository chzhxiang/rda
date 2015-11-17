;define(function(require){
    require('../../taurus/zoom/1.0.2/zoom.js')($);
    $('.cloud-zoom, .cloud-zoom-gallery').CloudZoom({
        zoomWidth: 'auto',
        zoomHeight: 'auto',
        position: 'right',
        tint: false,
        tintOpacity: 0.5,
        lensOpacity: 0.5,
        softFocus: false,
        smoothMove: 3,
        showTitle: true,//是否显示标题
        titleOpacity: 0.5,
        adjustX: 0,
        adjustY: 0
    });
});