/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.multithread;



/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MultiThreadTest {
	private static boolean ready;
	private static int number;
	private C c ;
	
	public MultiThreadTest(){
		c  = new C();
	}
	
	public C get(){
		return c;
	}

	static public void main(String[] args) throws InterruptedException {

		ReaderThread readerThread = new ReaderThread();
		readerThread.start();
//		readerThread.start();
		number = 42;
		ready = true;
	}

	private static class ReaderThread extends Thread {
		public void run() {
			while (!ready) {
				Thread.yield();
			}
			System.err.println(number);
		}
	}
	
	public static void main1(String[] args){
		new MultiThreadTest().get().print();
	}
	
	public void print(){
		System.err.println("threadTest");
	}
	
	class C{
		public void print(){
			System.err.println(MultiThreadTest.this.ready);
			System.err.println("C print test");
			MultiThreadTest.this.print();
		}
	}
}
