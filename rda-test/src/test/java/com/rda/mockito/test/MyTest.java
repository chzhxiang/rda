/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mockito.test;

import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MyTest {

	@Mock
	private MyMock m;

	@BeforeMethod
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		when(m.test()).thenReturn("mock test");
		when(m.test()).thenReturn("test 1", "test 2");
		System.out.println(m.test());
		System.out.println(m.test());
		when(m.test()).thenAnswer(new Answer() {
			    public Object answer(InvocationOnMock invocation) {
			        Object[] args = invocation.getArguments();
			        Object mock = invocation.getMock();
			        return "called with arguments: " + args;
			    }
			});
		System.out.println(m.test());
	}
	
	@Test
	public void testSpy(){
		List list = new LinkedList();
		List spy = spy(list);
		list.add("one");
		System.out.println(spy.get(0));
	}

	class MyMock {
		public String test() {
			return "my test";
		}
	}
}
