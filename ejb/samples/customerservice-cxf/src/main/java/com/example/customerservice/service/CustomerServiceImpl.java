package com.example.customerservice.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.customerservice.model.Customer;

@Transactional
public class CustomerServiceImpl implements CustomerService
{
	@PersistenceContext 
	protected EntityManager entityManager;
	public EntityManager getEntityManager() { return entityManager; }

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteCustomerById(final Integer customerId)
			throws NoSuchCustomerException {

		final Customer customer = getCustomerById(customerId);
		getEntityManager().remove(customer);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Customer getCustomerById(final Integer customerId)
			throws NoSuchCustomerException {

		final Customer customer = getEntityManager().find(Customer.class,
				customerId);

		if (customer == null) {
			NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
			noSuchCustomer.setCustomerId(customerId);
			throw new NoSuchCustomerException(
					"Did not find any matching customer for id [" + customerId
							+ "].", noSuchCustomer);

		} else {
			return customer;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Integer updateCustomer(Customer customer) {
		final Customer mergedCustomer = getEntityManager().merge(customer);
		return mergedCustomer.getCustomerId();
	}

}
