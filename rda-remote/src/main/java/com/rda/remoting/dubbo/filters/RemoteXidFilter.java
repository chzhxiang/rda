package com.rda.remoting.dubbo.filters;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.rda.core.support.ThreadContext;

/**
 * 此filter在consumer调用开始前将ThreadContext中的xid放入远程调用的context中,
 * 在provider调用开始前从远程调用context中取出放入ThreadContext中;
 * Created by lianrao on 2015/7/8.
 */
@Activate( group = {Constants.CONSUMER,Constants.PROVIDER} )
public class RemoteXidFilter implements Filter{
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if(RpcContext.getContext().isConsumerSide()){
            RpcContext.getContext().setAttachment(ThreadContext.XID_KEY,ThreadContext.getXid());
        }else{
            ThreadContext.setXid(RpcContext.getContext().getAttachment(ThreadContext.XID_KEY));
        }
        try {
            Result result = invoker.invoke(invocation);
            return result;
        }finally {
            if(RpcContext.getContext().isProviderSide()) {
                ThreadContext.removeXid();
            }
        }
    }
}
