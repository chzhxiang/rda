define("taurus/grid/1.0.0/grid-debug", ["taurus/querystring/1.0.0/querystring-debug"], function(require) {
    var qs = require('taurus/querystring/1.0.0/querystring-debug');
    return function($) {
        var defaultConfig = {
            paging : true,
            pageTheme : 'default',
            method : 'GET',
            root : 'data',
            pageSize : 10,
            showPageSize : false,
            start : 1,
            isREST : true,
            defaultParams : {},
            tableClass : 'gridTable',
            buttonWrapperClass : 'gridRowButton',
            buttonClass : 'gridButton',
            pageClass : 'gridPage'
        };
        $.fn.grid = function(options) {
            if (this.data('delegate') && !options)
                return this.data('delegate');
            var delegate = {
                query : query,
                refresh : refresh,
                setUrl : setUrl,
                setParam : setParam,
                render : render
            };
            this.data('delegate', delegate);
            var renderTo = this;
            var config = $.extend({}, defaultConfig, options);
            var current = 1;
            var pageSize = config.pageSize;
            var records;
            var params;
            if ( typeof config.defaultParams == 'string') {
                config.defaultParams = qs.parse(config.defaultParams);
            }
            params = {};
            params.start = config.start;
            params.end = config.start + pageSize - 1;
            params.pageSize = pageSize;
            $.extend(params, config.defaultParams);
            render();
            return delegate;

            function query(ps) {
                if ( typeof ps == 'string')
                    ps = qs.parse(ps);
                $.extend(params, ps);
                refresh();
            }

            function getData() {
                return records;
            }

            function setParam(key, value) {
                params[key] = value;
            }

            function setUrl(url) {
                config.url = url;
            }

            function refresh() {
                // 查询操作返回第一页
                if ( typeof config.defaultParams == 'string') {
                    config.defaultParams = qs.parse(config.defaultParams);
                }
                if (params.click) {
                    current = 1;
                }
                params.start = (current - 1) * pageSize + 1;
                params.end = params.start + pageSize - 1;
                params.pageSize = pageSize;
                render();
            }

            function render() {
                if (!config.paging) {
                    params.end = 99999;
                }
                $.ajax({
                    url : config.isREST ? $.restUrl(config.url, params) : config.url,
                    type : config.method,
                    data : (config.isREST && config.method.toUpperCase() == 'GET') ? {} : params,
                    success : function(data) {
                        var grid = $('<table />').addClass(config.tableClass);
                        records = data[config.root];
                        var head = getHead(config.fields);
                        grid.append(head);
                        var body = getBody(config.fields, data);
                        grid.append(body);
                        renderTo.empty();
                        renderTo.append(grid);
                        var pagingBar;
                        if (config.paging) {
                            if (config.pageTheme == 'Theme1') {
                                pagingBar = generateTheme1PagingBar(data);
                            } else {
                                pagingBar = generateDefaultPagingBar(data);
                            }
                            pagingBar.appendTo(renderTo);
                        }
                        if (config.buttons) {
                            var buttonWrap = $('<div />').addClass(config.buttonWrapperClass);
                            var buttonBox = $('<ul />').addClass(config.buttonClass).appendTo(buttonWrap);
                            $.each(config.buttons, function(i, button) {
                                var linkBox = $('<li />');
                                var link = $('<a href="#" />').appendTo(linkBox);
                                if (button.action) {
                                    link.click(function() {
                                        button.action.call(delegate, data);
                                        return false;
                                    });
                                } else {
                                    link.attr('href', button.href);
                                }
                                link.append($('<span />').html(button.text));
                                linkBox.appendTo(buttonBox);
                            });
                            buttonWrap.prependTo(renderTo);
                        }
                        if (config.callback) {
                            config.callback(data);
                        }
                    }
                });
            }

            function gotoPage(pageCount) {
                var value = $(".cur_page").val();
                current = parseInt(value) || 1;
                current = current > pageCount ? pageCount : current;
                current = current < 1 ? 1 : current;
                var gstart = (current - 1) * pageSize + 1, gend = gstart + pageSize - 1;
                if ( typeof config.defaultParams == 'string') {
                    config.defaultParams = qs.parse(config.defaultParams);
                }
                params.start = gstart;
                params.end = gend;
                params.pageSize = pageSize;
                render();
                return false;
            }

            function generateTheme1PagingBar(data) {
                var total = data.total;
                var pagingBar = $('<div />').addClass('gridBtn');
                var em = $('<em />');
                var pageCount = Math.ceil(total / pageSize);
                var pagingTool = $('<div />').addClass('gridPages').appendTo(em);
                if (config.extraContent || $.isFunction(config.extraContent)) {
                    pagingTool.append(config.extraContent(data));
                }
                em.appendTo(pagingBar);
                var info = '共&nbsp;' + total + '&nbsp;条 第&nbsp;' + current + '&nbsp;页&nbsp;/&nbsp;共&nbsp;' + pageCount + '&nbsp;页&nbsp;';
                pagingTool.append(info);
                if (pageCount > 1) {
                    var start = current - 2 < 1 ? 1 : current - 2;
                    var end = current + 2 > pageCount ? pageCount : current + 2;
                    if (current > 1) {
                        createPageLink(1, '首页', pagingTool);
                        createPageLink(current - 1, '&lt;', pagingTool);
                    }
                    for (var i = start; i <= end; i++) {
                        if (i != current) {
                            createPageLink(i, i, pagingTool);
                        } else {
                            var pageLink = $('<a />').html(i).appendTo(pagingTool).attr('index', i).addClass('focus');
                        }
                    }
                    if (current < end) {
                        createPageLink(current + 1, '&gt;', pagingTool);
                        createPageLink(pageCount, '尾页', pagingTool);
                    }
                    var pageInput = $('<input class="cur_page" size="4"/>').val(current).keyup(function(e) {
                        if (e.keyCode == 13)
                            gotoPage(pageCount);
                    });
                    var pageOk = $('<a href="javascript:void(0)"/>').html('确定').click(function() {
                        if (config.showPageSize) {
                            pageSize = parseInt($(".page_size").val()) || config.pageSize;
                            pageSize = pageSize < 1 ? config.pageSize : pageSize;
                        }
                        gotoPage(pageCount);
                    });
                    pagingTool.append('&nbsp;到第&nbsp;').append(pageInput).append('&nbsp;页&nbsp; ').append(pageOk);
                }
                return pagingBar;
            }

            function generateDefaultPagingBar(data) {
                var total = data.total;
                var pageCount = Math.ceil(total / pageSize);
                var pagingBar = $('<div />').addClass(config.pageClass);
                if (config.extraContent || $.isFunction(config.extraContent)) {
                    pagingBar.append(config.extraContent(data));
                }
                var info = '当前第' + current + '页/共' + pageCount + '页 总共记录数' + total + '条';
                pagingBar.append(info);
                if (pageCount > 1) {
                    var start = current - 2 < 1 ? 1 : current - 2;
                    var end = current + 2 > pageCount ? pageCount : current + 2;
                    if (current > 1) {
                        createPageLink(1, '首页', pagingBar);
                        createPageLink(current - 1, '上页', pagingBar);
                    }
                    if (current < end) {
                        createPageLink(current + 1, '下页', pagingBar);
                        createPageLink(pageCount, '尾页', pagingBar);
                    }
                    var pageInput = $('<input class="cur_page" size="4"/>').val(current).keyup(function(e) {
                        if (e.keyCode == 13)
                            gotoPage(pageCount);
                    });
                    var pageOk = $('<a href="javascript:void(0)"/>').html('确定').click(function() {
                        if (config.showPageSize) {
                            pageSize = parseInt($(".page_size").val()) || config.pageSize;
                            pageSize = pageSize < 1 ? config.pageSize : pageSize;
                        }
                        gotoPage(pageCount);
                    });
                    pagingBar.append('第').append(pageInput).append('页');
                    if (config.showPageSize) {
                        pagingBar.append(' 每页显示').append($('<input class="page_size" size="4"/>').val(pageSize)).append('条');
                    }
                    pagingBar.append(pageOk);
                }
                return pagingBar;
            }

            function createPageLink(index, html, pagingBar) {
                var pageLink = $('<a />').html(html).appendTo(pagingBar).attr('index', index);
                pageLink.attr('href', '#').click(function() {
                    current = parseInt($(this).attr('index'));
                    var istart = (current - 1) * pageSize + 1, iend = istart + pageSize - 1;
                    if ( typeof config.defaultParams == 'string') {
                        config.defaultParams = qs.parse(config.defaultParams);
                    }
                    params.start = istart;
                    params.end = iend;
                    params.pageSize = pageSize;
                    render();
                    return false;
                });
            }

            function getHead(fields) {
                var thead = $('<thead />');
                var rowHead = $('<tr />').appendTo(thead);
                $.each(fields, function(i, field) {
                    var th = $('<th />').appendTo(rowHead);
                    if (field.type == 'hidden')
                        th.hide();
                    if (field.type == 'checkbox') {
                        th.append($('<span />').addClass('checkbox').append($('<input />').attr('type', 'checkbox').change(function() {
                            thead.parent().find(':checkbox[name=' + field.id + ']').attr('checked', this.checked);
                        })));
                    } else {
                        var hd = $('<span />').html(field.name || '');
                        if (field.headClick && ( typeof field.headClick === 'function')) {
                            hd.click(field.headClick);
                        }
                        hd.appendTo(th);
                    }
                    field.title = th;
                });
                return thead;
            }

            function getBody(fields, data) {
                var tbody = $('<tbody />');
                if (config.sortable)
                    tbody.sortable();
                $.each(data[config.root], function(i, d) {
                    var seq = params.start + i;
                    var row = $('<tr />').appendTo(tbody);
                    $.each(config.fields, function(i, field) {
                        var value = d[field.id];
                        var html = value;
                        if (field.operation) {
                            field.render = function() {
                                var box = $('<div />');
                                $.each(field.operation, function(i, oper) {
                                    if ((oper.display && !$.isFunction(oper.display) && oper.display != 'N') || (oper.display && $.isFunction(oper.display) && oper.display.call(delegate, d)) || oper.display == undefined) {
                                        var link = $('<a href="#" />').attr('class', 'button_a').append($('<span />').html(oper.text)).click(function() {
                                            if (oper.confirm) {
                                                $.msg.confirm(oper.confirm, function(result) {
                                                    if (result) {
                                                        oper.action.call(delegate, d);
                                                    }
                                                });
                                            } else {
                                                oper.action.call(delegate, d);
                                            }
                                            return false;
                                        });
                                        box.append(link);
                                    }
                                });
                                return box.children();
                            };
                        }
                        if (field.options) {
                            var options = field.options;
                            field.render = function(data, id, value) {
                                return options[value];
                            };
                        }
                        if ($.isFunction(field.render)) {
                            html = field.render(d, field.id, value);
                        }
                        if (!html) html = "";
                        var td = $('<td />').appendTo(row);
                        if (field.type == 'date' &&　html) {
                            html = $.format.formatDate(new Date(html));
                        }
                        if (field.type == 'currency' &&　html) {
                            html = $.format.formatCurrency(html);
                        }
                        if (field.type == 'time' &&　html) {
                                html = $.format.formatTime(new Date(html));
                        }
                        field.checkboxCondition = field.checkboxCondition || true;
                        if (field.type == 'checkbox') {
                            if ((field.checkboxCondition && !$.isFunction(field.checkboxCondition)) || (field.checkboxCondition && $.isFunction(field.checkboxCondition) && field.checkboxCondition.call(delegate, d))) {
                                td.append($('<input />').attr({
                                    type : 'checkbox',
                                    name : field.id,
                                    value : value
                                }).change(function() {
                                    var grid = tbody.parent();
                                    if (!this.checked) {
                                        grid.find('th :checkbox').attr('checked', false);
                                    } else {
                                        var allChecked = true;
                                        grid.find('td :checkbox').each(function(index, elem) {
                                            if (!elem.checked)
                                                allChecked = false;
                                        });
                                        if (allChecked)
                                            grid.find('th :checkbox').attr('checked', true);
                                    }
                                }));
                            }
                        } else if (field.type == 'radio') {
                            // 支持radio
                            if (document.uniqueID) {
                                var _radio = document.createElement("<input type='radio' name='" + field.id + "' value='" + value + "'>");
                                td.append(_radio);
                            } else {
                                td.append($('<input />').attr({
                                    type : 'radio',
                                    name : field.id,
                                    value : value
                                }));
                            }
                        } else if (field.type == 'hidden') {
                            td.append($('<input />').attr({
                                type : 'hidden',
                                name : field.id,
                                value : value
                            }));
                            td.hide();
                        } else if (field.type == 'seq') {
                            td.append(seq);
                        } else {
                            if (field.maxlength) {
                                var shortHtml = $.abbreviate(html, field.maxlength);
                                td.html(shortHtml).attr('title', html);
                            } else {
                                td.html(html);
                            }
                        }
                        if (field.width) {
                            td.css('width', (field.width + '').indexOf('%') == -1 ? field.width + 'px' : field.width);
                        }
                        if (field.style) {
                            td.css(field.style);
                        }
                        field.body = td;
                    });
                });
                $('#ui-datepicker-div').hide();
                return tbody;
            }

        };
        $.restUrl = function(url, params) {
            var tokens = url.match(/ {(.* ? )} /g);
            $.each(tokens || [], function(i, token) {
                var key = token.substr(1, token.length - 2);
                var value = params[key] || '';
                url = url.replace(token, value);
            });
            return url;
        };
        $.abbreviate = function(str, length) {
            if ($.getLength(str) > length) {
                // ... 只占两个字母的位置左右
                return $.substr(str, 0, length - 2) + '...';
            } else {
                return str;
            }
        };
        $.substr = function(str, start, length) {
            var escapeStr = escape(str);
            var n = 0, current = 0, lastCurrent;
            while (n <= length) {
                lastCurrent = current;
                if (escapeStr.charAt(current) == '%') {
                    if (escapeStr.charAt(current + 1) == 'u') {
                        current += 6;
                        n += 2;
                    } else {
                        current += 3;
                        n++;
                    }
                } else {
                    current++;
                    n++;
                }
            }
            return unescape(escapeStr.substring(0, lastCurrent));
        };
        $.getLength = function(str) {
            if (!str)
                return 0;
            var m = escape(str).match(/ % u /g);
            return str.length + ( m ? m.length : 0);
        };
        $.format = ( function() {
                function parseMonth(value) {
                    switch (value) {
                        case "Jan":
                            return "01";
                        case "Feb":
                            return "02";
                        case "Mar":
                            return "03";
                        case "Apr":
                            return "04";
                        case "May":
                            return "05";
                        case "Jun":
                            return "06";
                        case "Jul":
                            return "07";
                        case "Aug":
                            return "08";
                        case "Sep":
                            return "09";
                        case "Oct":
                            return "10";
                        case "Nov":
                            return "11";
                        case "Dec":
                            return "12";
                        default:
                            return value;
                    }
                };

                function parseTime(value) {
                    var retValue = value;
                    if (retValue.indexOf(".") !== -1) {
                        retValue = retValue.substring(0, retValue.indexOf("."));
                    }

                    var values3 = retValue.split(":");

                    if (values3.length === 3) {
                        hour = values3[0];
                        minute = values3[1];
                        second = values3[2];
                        if (second.length > 2)
                            second = second.substring(0, 2);
                        return {
                            time : retValue,
                            hour : hour,
                            minute : minute,
                            second : second
                        };
                    } else {
                        return {
                            time : "",
                            hour : "",
                            minute : "",
                            second : ""
                        };
                    }
                };

                function date(value, format) {
                    // value = new java.util.Date()
                    // 2009-12-18 10:54:50.546
                    try {
                        var year = null;
                        var month = null;
                        var dayOfMonth = null;
                        var time = null;
                        // json, time, hour, minute, second
                        if ( typeof value.getFullYear === "function") {
                            year = value.getFullYear();
                            month = value.getMonth() + 1;
                            dayOfMonth = value.getDate();
                            time = parseTime(value.toTimeString());
                        } else {
                            var values = value.split(" ");

                            switch (values.length) {
                                case 6:
                                    // Wed Jan 13 10:43:41 CET 2010
                                    year = values[5];
                                    month = parseMonth(values[1]);
                                    dayOfMonth = values[2];
                                    time = parseTime(values[3]);
                                    break;
                                case 2:
                                    // 2009-12-18 10:54:50.546
                                    var values2 = values[0].split("-");
                                    year = values2[0];
                                    month = values2[1];
                                    dayOfMonth = values2[2];
                                    time = parseTime(values[1]);
                                    break;
                                default:
                                    return value;
                            }
                        }

                        var pattern = "";
                        var retValue = "";

                        for (var i = 0; i < format.length; i++) {
                            var currentPattern = format.charAt(i);
                            pattern += currentPattern;
                            switch (pattern) {
                                case "dd":
                                    retValue += ((dayOfMonth < 10 ? "0" : "") + dayOfMonth);
                                    pattern = "";
                                    break;
                                case "MM":
                                    retValue += ((month < 10 ? "0" : "") + month);
                                    pattern = "";
                                    break;
                                case "yyyy":
                                    retValue += year;
                                    pattern = "";
                                    break;
                                case "HH":
                                    retValue += time.hour;
                                    pattern = "";
                                    break;
                                case "hh":
                                    retValue += (time.hour === 0 ? 12 : time.hour < 13 ? time.hour : time.hour - 12);
                                    pattern = "";
                                    break;
                                case "mm":
                                    retValue += time.minute;
                                    pattern = "";
                                    break;
                                case "ss":
                                    retValue += time.second;
                                    pattern = "";
                                    break;
                                case "a":
                                    retValue += time.hour > 12 ? "PM" : "AM";
                                    pattern = "";
                                    break;
                                case " ":
                                    retValue += currentPattern;
                                    pattern = "";
                                    break;
                                case "/":
                                    retValue += currentPattern;
                                    pattern = "";
                                    break;
                                case ":":
                                    retValue += currentPattern;
                                    pattern = "";
                                    break;
                                default:
                                    if (pattern.length === 2 && pattern.indexOf("y") !== 0) {
                                        retValue += pattern.substring(0, 1);
                                        pattern = pattern.substring(1, 2);
                                    } else if ((pattern.length === 3 && pattern.indexOf("yyy") === -1)) {
                                        pattern = "";
                                    }
                            }
                        }
                        return retValue;
                    } catch (e) {
                        return value;
                    }
                }

                function parseCurrency(value) {
                    var html = value;
                    number = html + '';
                    numArr = number.split(".");
                    number = numArr[0];
                    tail = numArr[1];
                    if (!html) {
                        return '0.00';
                    }
                    if (numArr.length == 1) {
                        tail = '00';
                    } else {
                        tail = numArr[1].length == 1 ? numArr[1] + '0' : numArr[1];
                    }
                    if (number.length <= 3)
                        html = (number == '' ? '0' : number + '.' + tail);
                    else {
                        var mod = number.length % 3;
                        var output = (mod == 0 ? '' : (number.substring(0, mod)));
                        for (var i = 0; i < Math.floor(number.length / 3); i++) {
                            if ((mod == 0) && (i == 0))
                                output += number.substring(mod + 3 * i, mod + 3 * i + 3);
                            else
                                output += ',' + number.substring(mod + 3 * i, mod + 3 * i + 3);
                        }
                        html = (output + '.' + tail);
                    }
                    return html;
                }

                return {
                    date : date,
                    formatDate : function(value) {
                        return date(value, 'yyyy-MM-dd');
                    },
                    formatTime : function(value) {
                        return date(value, 'yyyy-MM-dd HH:mm:ss');
                    },
                    formatCurrency : function(value) {
                        return parseCurrency(value);
                    }
                };
            }());
    };
}); 