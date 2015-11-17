/**
 * @fileoverview 常用工具集.
 * @author binlic@isoftstone.com (李斌)
 */
define(function(require,exports, module) {
    'use strict';

	/**
	 * @description 字符串左右去除空格
	 * @returns 经过去空格后的字符串
	 * @demo " abc ".trim() ==> "abc"
	 */
	String.prototype.trim = function() { 
		return this.replace(/(^\s*)|(\s*$)/g, ""); 
	};  
	/**
	 * @description 字符串左侧去除空格
	 * @returns 经过去空格后的字符串
	 * @demo " abc ".ltrim() ==> "abc "
	 */
	String.prototype.ltrim = function() { 
		return this.replace(/(^\s*)/g, ""); 
	};  
	/**
	 * @description 字符串右侧去除空格
	 * @returns 经过去空格后的字符串
	 * @demo " abc ".rtrim() ==> " abc"
	 */
	String.prototype.rtrim = function() { 
		return this.replace(/(\s*$)/g, ""); 
	}; 
	/**
	 * @description 获取给定字符串的长度,包括回车换行符
	 * @returns 字符串长度
	 * @demo " abc ".getLengthrn() ==> 5
	 */
	String.prototype.getLengthrn = function() {  
		var length = this.length;
		if(this.indexOf("\n") != -1){
			var nrl = this.match(new RegExp("\n","g")).length;
			length +=  nrl;
		}
		return length;   
	};
	/**
	 * @description 用户补齐占位符的内容方法,根据内容位置匹配
	 * @returns 匹配好的字符串,可用于循环的<option>拼接以及复杂的字符串拼接
	 * @demo  "Name:{0} Sex:{1}".patchContent("Leon.Qiu","Male"); 
	 * ==>Name:Leon.Qiu Sex:Male
	 */
	String.prototype.patchContent = function() {
		var arg = arguments;
		return this.replace(/\{(\d+)\}/g, function(i, m) {
		return arg[m];
		});
		};
	/**
	 * @description 判断给定的字符串是否为中文
	 * @param word 目标字符串
	 * @returns true表示是中文，false表示非中文
	 * @demo isChinese("你好") ==> true
	 */
	exports.isChinese = function(word) {
		var lst = /[u00-uFF]/;
		return !lst.test(word);
	};
	/**
	 * @description 判断给定的字符串是否为email格式
	 * @param val 目标字符串
	 * @returns true表示格式正确，false表示格式错误
	 * @demo isEmail("binlic@isoftstone.com") ==> true
	 */
	exports.isEmail=function(val){
		return new RegExp("^.+\\@(\\[?)[a-zA-Z0-9\\-\\.]+\\.([a-zA-Z]{2,3}|[0-9]{1,3})(\\]?)$").test(val);
	};
	/**
	 * @description 字段截取，返回截取的从0个到n个的字符串，中文为2个字节长度，其他为1个字节
	 * @param n 需要截取最大长度
	 * @returns 截取后的字符串
	 * @demo "你好binlic@isoftstone.com".sub(5) ==> "你好b"
	 */
	String.prototype.sub = function(n)
	{    
		var r = /[^\x00-\xff]/g;    
		if(this.replace(r, "mm").length <= n) return this;     
		var m = Math.floor(n/2);    
		for(var i=m; i<this.length; i++) {    
		if(this.substr(0, i).replace(r, "mm").length>=n) {    
		   return this.substr(0, i) ; }    
		} 
		return this;   
	 };
	/**
	 * @description 如果字符串大于num个字符截取字符串前num个字符，后面省略显示
	 * @param str 目标字符串
	 * @param num 显示的最大长度
	 * @returns 省略后的字符串
	 * @demo shortDesc("abcdefg",2) ==> "ab..."
	 */
	exports.shortDesc=function(str,num){
		if(str==null || str=='')
			return str;
		if(str.match(/[^\x00-\xff]/ig)){
			var cArr = str.match(/[^\x00-\xff]/ig);
			if((str.length+cArr.length)<=num){
				return str;
			}else{
				return str.sub(num)+"...";
			}
		}else{
			if(str.length<=num){
				return str;
			}else{
				return str.sub(num)+"...";
			}
		}
	};
	/**
	 * @description 日期date格式化
	 * @param format 格式化形式,比如yyyy-MM-dd
	 * @returns 格式化后的日期
	 * @demo new Date().format('yyyy-MM-dd') ==> "2011-07-15"
	 */
	Date.prototype.format = function(format)
	{
	    var o =
	    {
	        "M+" : this.getMonth()+1, //month
	        "d+" : this.getDate(),    //day
	        "h+" : this.getHours(),   //hour
	        "m+" : this.getMinutes(), //minute
	        "s+" : this.getSeconds(), // cond
	        "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
	        "S" : this.getMilliseconds() //millisecond
	    };
	    if(/(y+)/.test(format))
	    format=format.replace(RegExp.$1,(this.getFullYear()+"").substr(4 - RegExp.$1.length));
	    for(var k in o)
	    if(new RegExp("("+ k +")").test(format))
	    format = format.replace(RegExp.$1,RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
	    return format;
	};
	/**
	 * @description 特殊字符转义
	 * @param s 目标字符串
	 * @returns 转义后的字符串
	 * @demo htmlEncode('id=1 & age<25') ==> "id=1&nbsp;&amp;&nbsp;age&lt;25"
	 */
	exports.htmlEncode = function(s) {
		if(s != null){
		    s = s.replace(new RegExp("&","g"), "&amp;");
		    s = s.replace(new RegExp("<","g"), "&lt;");
		    s = s.replace(new RegExp(">","g"), "&gt;");
		    s = s.replace(new RegExp("\"","g"), "&quot;");
		    s = s.replace(new RegExp("\'","g"), "&#34;");
		    s = s.replace(new RegExp(" ","g"), "&nbsp;");
		    s = s.replace(new RegExp("\n","g"), "<br/>");
		}
	    return s;
	};

	/**
	 * @description 等比例缩放图片
	 * @param img 图片Id
	 * @param width 限定最大宽度
	 * @param height 限定最大高度
	 */
	exports.setPopimage=function(img,width,height){
		var image = document.getElementById(img);
		if (image.width > image.height){
		   if(image.width>width){
		    image.width=width;
		    image.height=width/image.width*image.height;
		   }
		}else{
		   if(image.height>height){
		    image.height=height;
		    image.width=height/image.height*image.width;
		   }
		}
	};

	/**
	 * @description 格式化数据货币，以千分符分割，保留2位小数，例如1,000,000,000.00
	 * @param number 目标数值或字符串
	 * @returns 格式化后的字符串
	 * @demo formatCurrency(1000000000) ==> 1,000,000,000.00
	 */
    exports.formatCurrency = function(number){
    	number = number + '';
    	var numArr = number.split("."),behindDot;
    	number = numArr[0];
    	if(!numArr[1]){
    		behindDot = '00';
    	}else{
    		behindDot =  numArr[1].length==1 ? numArr[1]+'0':numArr[1];
    	}
    	if (number.length<= 3){
    		return (number == '' ? '0' : number+'.'+behindDot);
    	}else{
    		 var mod = number.length%3;
    		 var output = (mod == 0 ? '' : (number.substring(0,mod)));
    		 for (var i=0 ; i< Math.floor(number.length/3) ; i++)
    		 {
    		   if ((mod ==0) && (i ==0))
    		   output+= number.substring(mod+3*i,mod+3*i+3);
    		   else
    		   output+= ',' + number.substring(mod+3*i,mod+3*i+3);
    		 }
    		 return (output+'.'+behindDot);
    	}
    };

    /**
	 * @description 根据出身日期计算当前年龄
	 * @param birthDate 出生年月，必须是yyyy-MM-dd格式
	 * @returns 计算出的年龄
	 * @demo calcAge('1986-08-30') ==> 25
	 */
	exports.calcAge=function(birthDate){
		return Number(new Date().getFullYear()) - Number(birthDate.split('-')[0]);
	};

	/**
	 * @description 验证身份证
	 * @param value 身份证号码字符串
	 * @returns true表示正确,false表示错误
	 * @demo IDcorrect('320211198602300433') ==> false
	 */
	exports.IDcorrect=function(value) {
	 	value=value.trim();
	 	var Y,JYM;  
	    var S,M;  
	    var idcard_array = new Array();  
	    idcard_array = value.split("");
	 	var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"};
		if(value!=""&&value.length!=15&&value.length!=18){
			return false;
		}
	 	if(value!=""&&area[parseInt(value.substr(0,2))]==null){
	 		return false;
	 	}
        var ereg;
	 	switch(value.length){
	 	//15位身份证号校验
	 	case 15: 
	 		if ( (parseInt(value.substr(6,2))+1900) % 4 == 0 || ((parseInt(value.substr(6,2))+1900) % 100 == 0 && (parseInt(value.substr(6,2))+1900) % 4 == 0 )){ 
	 			ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/; 
	 		} else { 
	 			ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/; 
	 		}
	 		if(!ereg.test(value)){
	 			return false;	
	 		}
	 	break;
	 	//18位身份证号校验
	 	case 18: 
	 		if ( parseInt(value.substr(6,4)) % 4 == 0 || (parseInt(value.substr(6,4)) % 100 == 0 && parseInt(value.substr(6,4))%4 == 0 )){ 
	 			//闰年出生日期的合法性正则表达式 
	 			ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9xX]{1}$/;
	 		} else { 
	 			//平年出生日期的合法性正则表达式 
	 			ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9xX]{1}$/;
	 		}
	 		if(ereg.test(value)){//测试出生日期的合法性  
	 		     //计算校验位  
	 		     S = (parseInt(idcard_array[0]) + parseInt(idcard_array[10])) * 7  
	 		     + (parseInt(idcard_array[1]) + parseInt(idcard_array[11])) * 9  
	 		     + (parseInt(idcard_array[2]) + parseInt(idcard_array[12])) * 10  
	 		     + (parseInt(idcard_array[3]) + parseInt(idcard_array[13])) * 5  
	 		     + (parseInt(idcard_array[4]) + parseInt(idcard_array[14])) * 8  
	 		     + (parseInt(idcard_array[5]) + parseInt(idcard_array[15])) * 4  
	 		     + (parseInt(idcard_array[6]) + parseInt(idcard_array[16])) * 2  
	 		     + parseInt(idcard_array[7]) * 1   
	 		     + parseInt(idcard_array[8]) * 6  
	 		     + parseInt(idcard_array[9]) * 3 ;  
	 		     Y = S % 11;  
	 		     M = "F";  
	 		     JYM = "10X98765432";  
	 		     M = JYM.substr(Y,1);/*判断校验位*/
	 		    if(M == idcard_array[17].toUpperCase()){  
	 		       return true; /*检测ID的校验位false;*/  
	 		    }  
	 		    else {  
	 		       return false;  
	 		    }
	 		}
	 		else {
	 		     return false;  
	 		}  
	 	default:  
	 	     return true;
	 	}
	};

    /**
     * @description 比较两个日期相差天数
     * @param startDate 开始日期， yyyy-MM-dd格式
     * @param endDate 结束日期， yyyy-MM-dd格式
     * @returns 相差天数
     * @demo dateDiff('1999-01-01','2010-12-31') ==> 4382
     */
    exports.dateDiff=function(startDate,endDate){
        var times = startDate.split("-");
        var date1 = new Date(times[0],times[1]-1,times[2]);
        times = endDate.split("-");
        var date2 = new Date(times[0],times[1]-1,times[2]);
        var diffValue = date2.getTime() - date1.getTime();
        return parseInt(diffValue/86400000);
    };

	/**
	 * @description 计算两个数的和
	 * @param arg1 数字1
	 * @param arg2 数字2
	 * @returns 数字1+数字2的和
	 * @demo accAdd(1,1) ==> 2
	 */
	exports.accAdd=function(arg1,arg2){
		   var r1=arg1.toString(),r2=arg2.toString(),m;
		   if(!r1.split(".")[1]){r1=0;}else{r1=arg1.toString().split(".")[1].length;}
		   if(!r2.split(".")[1]){r2=0;}else{r2=arg2.toString().split(".")[1].length;}
		   m=Math.pow(10,Math.max(r1,r2));
		   return Number((arg1*m+arg2*m)/m);
    };

	Number.prototype.add = function (arg){
	   return exports.accAdd(arg,this);
	}; 
	/**
	 * @description 计算两个数的积
	 * @param arg1 数字1
	 * @param arg2 数字2
	 * @returns 数字1乘以数字2的积
	 * @demo accMutiply(1,1) ==> 1
	 */
	exports.accMutiply=function(arg1,arg2){
		   var r1=arg1.toString(),r2=arg2.toString(),m;
		   if(!r1.split(".")[1]){r1=0;}else{r1=arg1.toString().split(".")[1].length;}
		   if(!r2.split(".")[1]){r2=0;}else{r2=arg2.toString().split(".")[1].length;}
		   m=Math.pow(10,Math.max(r1,r2));
		   return Number((arg1*m)*(arg2*m)/Math.pow(m,2));
	};

	Number.prototype.mutiply = function (arg){
	   return exports.accMutiply(arg,this);
	};
	/**
	 * @description 计算两个数的差
	 * @param arg1 数字1
	 * @param arg2 数字2
	 * @returns 数字1减去数字2的差
	 * @demo accSubtract(1,1) ==> 0
	 */
	exports.accSubtract=function(arg1,arg2){
		   var r1=arg1.toString(),r2=arg2.toString(),m;
		   if(!r1.split(".")[1]){r1=0;}else{r1=arg1.toString().split(".")[1].length;}
		   if(!r2.split(".")[1]){r2=0;}else{r2=arg2.toString().split(".")[1].length;}
		   m=Math.pow(10,Math.max(r1,r2));
		   return Number((arg2*m-arg1*m)/m);
	};

	Number.prototype.subtract = function (arg){
		   return exports.accSubtract(arg,this);
	};
	/**
	 * @description 计算两个数的商
	 * @param arg1 数字1
	 * @param arg2 数字2
	 * @returns 数字1除以数字2的商
	 * @demo accDivide(2,1) ==> 2
	 */
	exports.accDivide=function(arg1,arg2){
		   var r1=arg1.toString(),r2=arg2.toString(),m;
		   if(!r1.split(".")[1]){r1=0;}else{r1=arg1.toString().split(".")[1].length;}
		   if(!r2.split(".")[1]){r2=0;}else{r2=arg2.toString().split(".")[1].length;}
		   m=Math.pow(10,Math.max(r1,r2));
		   return Number((arg2*m)/(arg1*m));
	};

	Number.prototype.divide = function (arg){
			   return exports.accDivide(arg,this);
	};
		
	/**
	 * @description 将“a=1&b=2&c=3”形式的queryString转换成json对象
	 * @param data 目标字符串
	 * @returns 转换好的json对象
	 * @demo convertQueryStringToJson("a=1&b=2") ==> {"a":1,"b":2}
	 */
	exports.convertQueryStringToJson=function(data){
		var container={};
		var array = data.split('&');
		for(var i=0;i<array.length;i++){
			var temp = array[i].split('=');
			container[temp[0]]=temp[1];
		}
		return container;
	};
	
	/**
	 * @description 用于解决定义json对象时无法使用变量作为key的情况
	 * @param key1,value1,key2,value2 ...... 必须是偶数个，在json对象中以key:value对的形式展示
	 * @returns json对象
	 * @demo 
	 * 	var a = "aStr",b = "bStr";
	 * 	generateJson{a,function(){alert(1);},b,function(){alert(2);}}
	 * 	==> {aStr:function(){alert(1);},bStr:function(){alert(2);}}
	 */
	exports.generateJson = function(){
		var argLen = arguments.length;
		if(argLen % 2 != 0) throw "Odd params";
		var tmpStr = '';
		for(var i=0;i<argLen;i++){
			if(i%2==0){
			  tmpStr += i==0? arguments[i]+'=' : '&'+arguments[i]+'=';
			}else{
			  tmpStr += 1;
			}
		}
		var json = exports.convertQueryStringToJson(tmpStr);
		for(var j=0;j<argLen;j++){
			if(j%2==1){
				json[arguments[j-1]]=arguments[j];
			}
		}
		return json;
	};
});