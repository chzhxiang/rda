;define(function(require){
    require('../../taurus/dialog/1.10.0/dialog.js')($);
    $.msg.alert('title',"This is content!",function(){
        alert('Hello world!');
    });
});