package com.hzsca.security;

public class AuthPrincipal {
    private final Long id;
    private final String type;

    public AuthPrincipal(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
