/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.tx.programmatic;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;


/**
 * <P>手工控制事务基础服务类</P>
 * @author lianrao
 */
public abstract class ProgrammaticTxBaseService {

	private final TransactionTemplate transactionTemplate;

	public ProgrammaticTxBaseService(PlatformTransactionManager txManager) {
		Assert.notNull(txManager, "The 'transactionManager' argument must not be null.");
		this.transactionTemplate = new TransactionTemplate(txManager);
	}
	
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object someServiceMethod() {
		    return transactionTemplate.execute(new TransactionCallback() {

		      // the code in this method executes in a transactional context
		      public Object doInTransaction(TransactionStatus status) {
		    	  //update operation
		        return null;
		      }
		    });
		  }
}
