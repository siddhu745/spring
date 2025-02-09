package com.siddhu.spring.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
