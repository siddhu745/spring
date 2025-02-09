package com.siddhu.spring.auth;

import com.siddhu.spring.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
