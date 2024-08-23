package com.example.common_module.member.domain.entity;

public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_MANAGER("MANAGER");

    // "USER", "ADMIN", "MANAGER"
    private String value;

    // Constructor
    Role(String value) {
        this.value = value;
    }

    // GetValue
    public String getValue() {
        return this.value;
    }
}
