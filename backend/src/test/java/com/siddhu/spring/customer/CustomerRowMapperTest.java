package com.siddhu.spring.customer;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("siddhuBoy");
        when(resultSet.getDate("date")).thenReturn(new Date(2002,12,28));
        when(resultSet.getString("gender")).thenReturn("male");

        //When
        var actual = customerRowMapper.mapRow(resultSet, 1);

        Customer customer = new Customer(
                1,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );

        //Then
        assertThat(actual).isEqualTo(customer);
    }
}