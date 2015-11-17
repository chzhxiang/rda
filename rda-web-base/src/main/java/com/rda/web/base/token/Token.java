package com.rda.web.base.token;

import com.rda.util.UUIDShort;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class Token
        implements Serializable {
    private static final long serialVersionUID = -2620912281214255410L;
    private String key;
    private Date expiredTime;

    public Token(){

    }

    public Token(String key, Date expiredTime) {
        this.key = key;
        this.expiredTime = expiredTime;
    }

    /**
     *默认生成10分钟后失效的token
     * @return
     */
    public static Token build() {
        String key = UUIDShort.Generate();
        Date expiredTime = DateTime.now().plusMinutes(10).toDate();
        Token token = new Token(key, expiredTime);
        return token;
    }

    /**
     * 设置此token在指定分钟后失效
     * @param minutes
     * @return
     */
    public  Token expiredAfterMins(int minutes){
       this.expiredTime = DateTime.now().plusMinutes(minutes).toDate();
        return this;
    }

    /**
     * 在生成的key值前增加前缀,可以不加
     * @param prefix
     * @return
     */
    public Token prefixKey(String prefix){
        this.key = prefix + this.key;
        return this;
    }

    /**
     * 判断此token是否已经失效
     * @return
     */
    public boolean isValid(){
        return new DateTime(this.expiredTime).isBeforeNow();
    }


    public String toString() {
        StringBuilder builder = new StringBuilder(96);
        builder.append("Token [key=").append(this.key).append(", exipried time =").append(this.expiredTime).append("]");
        return builder.toString();
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public String getKey() {
        return key;
    }

}

