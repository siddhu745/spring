package com.siddhu.spring.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJdbcDataAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJdbcDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> getCustomers() {
        String sql = "SELECT * FROM customer";
        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> getCustomer(Integer id) {
        String sql = "SELECT * FROM customer where id = ?";

        return jdbcTemplate.query(sql,customerRowMapper,id)
                .stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer (name,date,gender)
                VALUES(
                ?,?,?
                )""" ;
        jdbcTemplate.update(sql,customer.getName(),
                customer.getDate(),
                customer.getGender());
    }

    @Override
    public boolean existsPersonWithId(Integer customerId) {
        var sql = """
                SELECT count(id) FROM customer WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,customerId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithName(String name) {
        var sql = """
                SELECT count(name) FROM customer WHERE name = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,name);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomer(Integer id) {
        String sql = """
                DELETE FROM customer WHERE id = ?
                """;
        jdbcTemplate.update(sql,id);
    }

    @Override
    public void updateCustomer(Customer update) {
        if(update.getName() != null) {
            String sql = """
                    UPDATE customer SET name = ? WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql,update.getName(),update.getId());
            System.out.println("update customer name result = " + result);
        }

        if(update.getDate() != null) {
            String sql = """
                    UPDATE customer SET date = ? WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql,update.getDate(),update.getId());
            System.out.println("update customer date result = " + result);
        }

        if(update.getGender() != null) {
            String sql = """
                    UPDATE customer SET gender = ? WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql,update.getGender(),update.getId());
            System.out.println("update customer gender result = " + result);
        }


    }
}
