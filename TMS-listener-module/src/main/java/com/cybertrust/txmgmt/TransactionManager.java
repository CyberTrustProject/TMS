package com.cybertrust.txmgmt;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cybertrust.tmslistener.config.HibernateUtil;

public abstract class TransactionManager {

	public static abstract class TransactionCallable<T> {
	    public abstract T execute(Session session);
	}
	
	public static <T> T doInTransaction(TransactionCallable<T> callable) {
		T result = null;
		Transaction txn = null;
		try(Session session = HibernateUtil.getSession();) {
			txn = session.beginTransaction();
			
			try {
				result = callable.execute(session);
				txn.commit();
			}
			catch (Exception e) {
				System.err.println("Message Consumer: Error in updating trust levels, aborting transaction");
				txn.rollback();
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			System.err.println("Message Consumer: Cannot establish connection to the database");
			e.printStackTrace();
		}
		return result;
	}


}
