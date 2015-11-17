;define(function(require){
    require('art-dialog')($);
    /* 一、使用传统的参数 */
    $("#btn1").click(function () {
        $.dialog('简单愉悦的接口，强大的表现力，优雅的内部实现', function(){alert('yes');});
    });

    /* 二、使用字面量传参 */
    $("#btn2").click(function () {
        var dialog = $.dialog({
            title: '欢迎',
            content: '欢迎使用artDialog对话框组件！',
            icon: '../../../../style/art-dialog/skins/icons/succeed',
            follow: document.getElementById('btn2'),
            ok: function(){
                this.title('警告').content('请注意artDialog两秒后将关闭！').lock().time(2);
                return false;
            }
        });
    });

    /* 三、右下角滑动通知 */
    $("#btn3").click(function () {
        $.dialog.notice({
            title: '万象网管',
            width: 220,// 必须指定一个像素宽度值或者百分比，否则浏览器窗口改变可能导致artDialog收缩
            content: '尊敬的顾客朋友，您IQ卡余额不足10元，请及时充值',
            icon: '../../../../style/art-dialog/skins/icons/face-sad',
            time: 5
        });
    });
});