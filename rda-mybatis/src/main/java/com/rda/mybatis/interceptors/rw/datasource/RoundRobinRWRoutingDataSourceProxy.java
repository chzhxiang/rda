package com.rda.mybatis.interceptors.rw.datasource;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

public class RoundRobinRWRoutingDataSourceProxy extends AbstractRWRoutingDataSourceProxy {

    private AtomicInteger count = new AtomicInteger(0) ;
    @Override
    protected String determineCurrentLookupKey() {
       //NOOP just ignore
        return null;
    }

    @Override
    protected DataSource loadBalance() {
        int index = Math.abs(count.incrementAndGet())% getReadDsSize();
        return getResolvedReadDataSources().get(index);
    }

    
}
