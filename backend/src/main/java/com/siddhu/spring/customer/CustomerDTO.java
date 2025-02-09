package com.siddhu.spring.customer;

import java.sql.Date;
import java.util.List;

public record CustomerDTO(
        String name,
        Date date,
        String gender,
        List<String> roles,
        String username,
        Integer id,
        String profileImageId) {
}
