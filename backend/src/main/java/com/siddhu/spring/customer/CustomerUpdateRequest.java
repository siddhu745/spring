package com.siddhu.spring.customer;

import java.sql.Date;

public record CustomerUpdateRequest(
        String name,
        Date date,
        String gender
) {
}
