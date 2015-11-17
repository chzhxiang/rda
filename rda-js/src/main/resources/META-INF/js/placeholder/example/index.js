;define(function(require){
    require('../src/placeholder.js')($);
    $('#b-1').click(function(){
        alert($('#i-1').val());
    });
});