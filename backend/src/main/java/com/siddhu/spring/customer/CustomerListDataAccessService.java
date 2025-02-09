package com.siddhu.spring.customer;

import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{
	private static final List<Customer> customers = new ArrayList<>();
	static {
		customers.add(
				new Customer(
						1,
						"Alex",
                        "password", new Date(12-2-24),
						"male"
				));

		customers.add(
				new Customer(
						2,
						"jamila",
                        "password", new Date(12-2-24),
						"female"
				));

	}
	@Override
	public List<Customer> getCustomers() {
		return customers;
	}

	@Override
	public Optional<Customer> getCustomer(Integer id) {
		return customers.stream()
				.filter(
						p -> p.getId().equals(id)
				)
				.findFirst();
	}

	@Override
	public void insertCustomer(Customer customer) {
		customers.add(customer);
	}

	@Override
	public boolean existsPersonWithId(Integer id) {
		return customers.stream().anyMatch(
				c -> c.getId().equals(id)
		);
	}

	@Override
	public boolean existsPersonWithName(String name) {
		return customers.stream().anyMatch(
				c -> c.getName().equals(name)
		);
	}


	@Override
	public void deleteCustomer(Integer id) {
		customers.removeIf(c -> c.getId().equals(id));
	}

	@Override
	public void updateCustomer(Customer update) {
		customers.add(update);
	}

	@Override
	public Optional<Customer> getCustomerWithName(String name) {
		return customers.stream()
				.filter(
						p -> p.getName().equals(name)
				)
				.findFirst();
	}

	@Override
	public void updateCustomerProfileImageId(String profileImageId, Integer id) {

	}


}
