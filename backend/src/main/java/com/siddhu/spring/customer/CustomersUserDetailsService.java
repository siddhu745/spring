package com.siddhu.spring.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomersUserDetailsService implements UserDetailsService {

    public final CustomerDao customerDao;

    public CustomersUserDetailsService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Customer customer = customerDao.getCustomerWithName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        System.out.println(customer);
        return customer;
    }
}
