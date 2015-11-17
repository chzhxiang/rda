/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.multithread;

import java.util.Timer;
import java.util.TimerTask;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class TimeTaskTest {

	
	static public void main(String[] args) throws InterruptedException{
		Timer timer = new Timer();
		timer.schedule(new ThrowTask(), 1);
		Thread.sleep(1000);
		timer.schedule(new ThrowTask(), 1);
		Thread.sleep(5000);
		
	}
	
	static class ThrowTask extends TimerTask{

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 * @author lianrao
		 */
		@Override
		public void run() {
			throw new RuntimeException();
			
		}
		
	}
}
