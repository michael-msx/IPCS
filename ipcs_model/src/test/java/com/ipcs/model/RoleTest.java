package com.ipcs.model;


import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/Services.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class })
@DatabaseSetup(value="/original/role.xml", type= DatabaseOperation.REFRESH)
public class RoleTest
{
	@Resource
	SessionFactory sessionFactory;

	@Test
	@ExpectedDatabase(value= "/expected/role.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(value= "/original/role.xml",type = DatabaseOperation.DELETE_ALL)
	public void testInsertRole() {
    	Session session = sessionFactory.openSession();
		session.beginTransaction();
		Role role = new Role();
		role.setName("PARENT");
		Permission permission = (Permission)session.get(Permission.class,1l);
		Permission permissionActivity = (Permission)session.get(Permission.class,2l);
		role.addPermission(permission);
		role.addPermission(permissionActivity);
		session.save(role);
		session.getTransaction().commit();
		session.close();
	}

	@Test
	@DatabaseTearDown(value= "/original/role.xml",type = DatabaseOperation.DELETE_ALL)
	public void testDeleteRole() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Role role = (Role)session.get(Role.class, 1l);
		session.delete(role);
		session.getTransaction().commit();
		session.close();

		session = sessionFactory.openSession();
		session.beginTransaction();
		role = (Role)session.get(Role.class, 1l);
		Assert.assertNull(role);
		session.getTransaction().commit();
		session.close();
	}


}
