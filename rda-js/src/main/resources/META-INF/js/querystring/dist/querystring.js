define("taurus/querystring/1.0.0/querystring",[],function(e,t){function o(e){return e&&n.call(e)==="[object Object]"&&"isPrototypeOf"in e}function u(e){return e!==Object(e)}t.version="1.0.0";var n=Object.prototype.toString,r=Object.prototype.hasOwnProperty,i=Array.isArray||function(e){return n.call(e)==="[object Array]"},s=String.prototype.trim?function(e){return e==null?"":String.prototype.trim.call(e)}:function(e){return e==null?"":e.toString().replace(/^\s+/,"").replace(/\s+$/,"")};t.escape=encodeURIComponent,t.unescape=function(e){return decodeURIComponent(e.replace(/\+/g," "))},t.stringify=function(e,n,s,a){if(!o(e))return"";n=n||"&",s=s||"=",a=a||!1;var f=[],l,c,h=t.escape;for(l in e){if(!r.call(e,l))continue;c=e[l],l=t.escape(l);if(u(c))f.push(l,s,h(c+""),n);else if(i(c)&&c.length)for(var p=0;p<c.length;p++)u(c[p])&&f.push(l,(a?h("[]"):"")+s,h(c[p]+""),n);else f.push(l,s,n)}return f.pop(),f.join("")},t.parse=function(e,n,o){var u={};if(typeof e!="string"||s(e).length===0)return u;var a=e.split(n||"&");o=o||"=";var f=t.unescape;for(var l=0;l<a.length;l++){var c=a[l].split(o),h=f(s(c[0])),p=f(s(c.slice(1).join(o))),d=h.match(/^(\w+)\[\]$/);d&&d[1]&&(h=d[1]),r.call(u,h)?(i(u[h])||(u[h]=[u[h]]),u[h].push(p)):u[h]=d?[p]:p}return u}});