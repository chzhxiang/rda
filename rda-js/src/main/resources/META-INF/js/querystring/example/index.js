;define(function(require){
    var qs = require('../../taurus/querystring/1.0.0/querystring.js');
    var obj = {"name": "lee","age":25 } , str;
    document.getElementById('stringify').innerHTML=(str=qs.stringify(obj));
    document.getElementById('str').innerHTML=(str);
    var o = qs.parse(str);
    document.getElementById('parse').innerHTML='{"name":'+ o.name+',"age":'+ o.age +'}';
});