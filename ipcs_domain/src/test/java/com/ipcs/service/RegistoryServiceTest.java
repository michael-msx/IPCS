/**
 * 
 */
package com.ipcs.service;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ipcs.model.Person;

/**
 * @author Chen Chao
 *
 */
public class RegistoryServiceTest {

    RegistoryService registoryService = null;
    List<Person> adminAndStudents = null;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public void setUp() {
	ApplicationContext appContext = new ClassPathXmlApplicationContext(
		"Services.xml");
	registoryService = (RegistoryService) appContext
		.getBean("registoryServiceImpl");
	
    }

    @Test
    public void insertPerson() {
	
    }
    
    @Test(dependsOnMethods = {"insertAdmin"})
    public void testFindAllStudents(){
	
    }
    
    @AfterClass
    public void tearDown(){
	
    }

}
