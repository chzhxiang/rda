/*******************************************************************************
 * Copyright 2011 Soofa Team(www.soofa.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.rda.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <P>用于创建soofa特定格式的名称的线程</P>
 * 
 * @author 汪一鸣
 */
public class NamedThreadFactory implements ThreadFactory {

	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	private final AtomicInteger threadNumber = new AtomicInteger(1);

	private final String namePrefix;

	private final boolean isDaemon;

	private final ThreadGroup threadGroup;

	public NamedThreadFactory() {
		this("pool-" + POOL_SEQ.getAndIncrement(), false);
	}

	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemo) {
		namePrefix = prefix + "-thread-";
		isDaemon = daemo;
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 * @author 汪一鸣
	 */
	public Thread newThread(Runnable r) {
		String name = namePrefix + threadNumber.getAndIncrement();
		Thread ret = new Thread(threadGroup, r, name, 0);
		ret.setDaemon(isDaemon);
		return ret;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

}
