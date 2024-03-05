package com.siddhu.spring.customer;

import java.sql.Date;

public record CustomerRegistrationRequest(
        String name,
        Date date,
        String gender
) {
}
