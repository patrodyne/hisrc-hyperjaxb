package org.jvnet.hyperjaxb.ejb.cascadedids.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.Serializable;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.cascadedids.tests.Department.DepartmentId;
import org.jvnet.hyperjaxb.ejb.test.AbstractEntityManagerTest;

public class EntityManagerTest extends AbstractEntityManagerTest
{
	@Test
	public void testIt()
		throws Exception
	{
		final Company company = new Company();
		company.setId(1);
		company.setName("Company");
		
		final Department department1 = new Department();
		department1.setDepartmentId(1001);
		department1.setCompany(company);
		department1.setName("Department 1");
		company.getDepartments().add(department1);
		
		save(company);
		
		final Company company_ = load(Company.class, 1L);
		assertNotNull(company_, "Company not loaded.");
		
		final Department department1_ = load(Department.class, new DepartmentId(1, 1001));
		assertNotNull(department1_, "Department not loaded.");
		
		final Company department1Company_ = department1_.getCompany();
		assertNotNull(department1Company_, "Department company not loaded.");
	}

	public void save(Object object)
	{
		final EntityManager em = createEntityManager();
		em.getTransaction().begin();
		em.merge(object);
		em.getTransaction().commit();
		em.clear();
		em.close();
	}

	public <T> T load(Class<T> theClass, Serializable id)
	{
		final EntityManager em = createEntityManager();
		final T object = em.find(theClass, id);
		em.close();
		return object;
	}
}
