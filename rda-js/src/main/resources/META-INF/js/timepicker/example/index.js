;define(function(require){
    require('../../taurus/timepicker/1.1.1/timepicker.js')($);
    require('../../taurus/spinner/1.10.0/jquery.ui.spinner.js')($);
    $('#slider_example_1').datetimepicker();
    $('#slider_example_2').timepicker({
          hourGrid: 4,
          minuteGrid: 10,
          timeFormat: 'hh:mm tt'
      });
    $('#slider_example_3').datetimepicker({
                                              controlType: 'select',
                                              timeFormat: 'hh:mm tt'
                                          });

    var myControl=  {
        create: function(tp_inst, obj, unit, val, min, max, step){
            $('<input class="ui-timepicker-input" value="'+val+'" style="width:50%">')
                .appendTo(obj)
                .spinner({
                             min: min,
                             max: max,
                             step: step,
                             change: function(e,ui){ // key events
                                 tp_inst._onTimeChange();
                                 tp_inst._onSelectHandler();
                             },
                             spin: function(e,ui){ // spin events
                                 tp_inst.control.value(tp_inst, obj, unit, ui.value);
                                 tp_inst._onTimeChange();
                                 tp_inst._onSelectHandler();
                             }
                         });
            return obj;
        },
        options: function(tp_inst, obj, unit, opts, val){
            if(typeof(opts) == 'string' && val !== undefined)
                return obj.find('.ui-timepicker-input').spinner(opts, val);
            return obj.find('.ui-timepicker-input').spinner(opts);
        },
        value: function(tp_inst, obj, unit, val){
            if(val !== undefined)
                return obj.find('.ui-timepicker-input').spinner('value', val);
            return obj.find('.ui-timepicker-input').spinner('value');
        }
    };

    $('#slider_example_4').datetimepicker({
                                              controlType: myControl
                                          });
});