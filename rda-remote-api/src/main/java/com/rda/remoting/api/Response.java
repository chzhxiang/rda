package com.rda.remoting.api;

import java.io.Serializable;

/**
 * Created by lianrao on 2015/8/11.
 */
public class Response<E> implements Serializable {

    public static final String SUCCESS = "0000";
    private String xid;
    private String code;
    private String msg;
    private E data;

    public boolean success(){
       return SUCCESS.equalsIgnoreCase(this.code);
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    @Override
    public String toString() {
        return "Response{" +
                "xid='" + xid + '\'' +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
