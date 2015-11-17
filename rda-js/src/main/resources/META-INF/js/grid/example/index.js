;define(function(require){
    require('../../taurus/grid/1.0.0/grid.js')($);
    var grid = $("#content").grid({
                      url:'./region.json', //后台查询数据的url
                      defaultParams:$("#commentForm").serialize(),//查询参数
                      paging: true, //是否需要显示分页
                      root:"regions", //后台数据key名称
                      method:'GET', //请求方法
                      pageSize:10, //如显示分页，每页显示多少条数据
                      pageTheme:'Theme1', //分页样式
                      isREST:false,//是否支持rest风格
                      extraContent:function(data){
                          return $("<span>").html("这是自定义内容").css({
                                     'margin-right':50,
                                     'float':'left'
                                 });
                      },
                      fields:[//显示列
                          {type:'checkbox',id:'id'},//id选项表示当前input的value、name等于选项值，比如这里是id号
                          {type:'radio',id:'id'},//type选项表示不同的类型，本实例中所有不同的type都已覆盖
                          {type:'hidden',id:'id'},
                          {type:"seq",name:"编号",width:30},
                          {name:"区域代码",id:"code",width:60},//name是显示列名，id是
                          {name:"区域名称",id:"name",width:100},
                          {name:"省/市",width:120,render:function(response){//render为自定义渲染方法，return的值就是最终显示
                              var pid = response.parentId;
                              var result;
                              if(pid==1){
                                  result = "省";
                              }else if(pid==2){
                                  result = "市";
                              }else{
                                  result = "区";
                              }
                              return result;
                          }},
                          {name:"省/市",width:120,id:"parentId",options:{1:"省",2:"市"}},//options选项适用于枚举的场合
                          {type:'time',id:'createDate',name:"创建日期(time)",width:120},
                          {type:'date',id:'createDate',name:"创建日期(date)",width:120},
                          {type:'currency',id:'code',name:"金额"},
                          {name:"操作",operation:[{   //operation适用于超链接，按钮等需要出发事件的标签
                              text:"点击我",//按钮名称
                              display:true,//是否显示
                              action:function(data){//点击触发的事件
                                  alert(data.id + "--" + data.name);
                              }
                          }]
                          }
                      ]
                  });
        $("#query").click(function(){
            grid.query($("#commentForm").serialize());
            return false;
        });
});