;define(function(require){
    require('../../taurus/button/1.10.0/jquery.ui.button.js')($);
    //默认
    $('input[type=submit], a, button','#default').button()
        .click(function( event ) {//阻止a标签链接的默认事件
                   event.preventDefault();
               });

    //radio
    $( "#radio" ).buttonset();

    //checkbox
    $( "#check" ).button();
    $( "#format" ).buttonset();
});