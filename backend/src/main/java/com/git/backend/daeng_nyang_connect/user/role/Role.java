package com.git.backend.daeng_nyang_connect.user.role;

public enum Role {


    USER("USER"),

    ADMIN("ADMIN");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
