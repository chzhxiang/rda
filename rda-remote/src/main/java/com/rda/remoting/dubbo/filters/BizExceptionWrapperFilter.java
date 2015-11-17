package com.rda.remoting.dubbo.filters;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.rda.core.exception.FastRuntimeException;
import com.rda.remoting.api.Response;

/**
 * Created by lianrao on 2015/8/12.
 */
@Activate(
        group = {Constants.PROVIDER},
        order = -20000
)
public class BizExceptionWrapperFilter implements Filter {
        @Override
        public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
                try{
                        Result result = invoker.invoke(invocation);
                        return result;
                }catch (FastRuntimeException e){
                        Response<String> response = new Response<>();
                        response.setMsg("fast runtime exception");
                        response.setXid("abc");
                        response.setCode("failure");
                        Result result = new RpcResult(response);
                        return result;
                }
        }
}
