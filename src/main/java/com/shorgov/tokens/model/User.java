package com.shorgov.tokens.model;

import java.util.Set;

public record User(
        String name,
        String email,
        String password,
        Set<Role> roles
) {
}
