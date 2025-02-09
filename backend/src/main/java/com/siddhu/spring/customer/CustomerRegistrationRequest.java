package com.siddhu.spring.customer;

import java.sql.Date;

public record CustomerRegistrationRequest(
        String name,
        String password,
        Date date,
        String gender
) {
}
