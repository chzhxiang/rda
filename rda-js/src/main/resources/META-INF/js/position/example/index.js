;define(function(require){
    require('../../taurus/position/1.10.0/jquery.ui.position.js')($);
    function position() {
        $( ".positionable" ).position({
                                          of: $( "#parent" ),
                                          my: $( "#my_horizontal" ).val() + " " + $( "#my_vertical" ).val(),
                                          at: $( "#at_horizontal" ).val() + " " + $( "#at_vertical" ).val(),
                                          offset: $( "#offset" ).val(),
                                          collision: $( "#collision_horizontal" ).val() + " " + $( "#collision_vertical" ).val()
                                      });
    }

    $( ".positionable" ).css( "opacity", 0.5 );

    $( "select, input" ).bind( "click keyup change", position );

    $( "#parent" ).draggable({
                                 drag: position
                             });

    position();
});