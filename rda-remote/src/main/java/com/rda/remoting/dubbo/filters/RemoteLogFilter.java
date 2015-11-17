package com.rda.remoting.dubbo.filters;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jmx.snmp.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * log远程调用的各个变量
 * Created by lianrao on 2015/3/13.
 */
@Activate(
        group = {Constants.CONSUMER},
        order = -20000
)
public class RemoteLogFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static ObjectMapper objectMapper ;

    static {
       objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Object[] arguments = invocation.getArguments();
        Result var3;
        try {
            String param = objectMapper.writeValueAsString(arguments);
            log.debug("开始调用接口:[{}:{}],参数:[{}]",invoker.getInterface(), invocation.getMethodName(),param);
        } catch (JsonProcessingException e) {
            log.error("调用接口:[{}:{}]时输入参数转为json日志格式时出错:",invoker.getInterface(),invocation.getMethodName(),e);
        }
        var3 = invoker.invoke(invocation);
        try {
            String res = objectMapper.writeValueAsString(var3);
            if(StringUtils.hasText((String) ThreadContext.get("trace"))){
	            log.info("结束调用接口:[{}:{}],返回数据:[{}]",invoker.getInterface(), invocation.getMethodName(),res);
            }else{
	            log.debug("结束调用接口:[{}:{}]",invoker.getInterface(), invocation.getMethodName());
            }
        } catch (JsonProcessingException e) {
            log.error("调用接口:[{}:{}],返回数据转为json日志格式时出错:",invoker.getInterface(),invocation.getMethodName(),e);
        }
        return var3;
    }
}
