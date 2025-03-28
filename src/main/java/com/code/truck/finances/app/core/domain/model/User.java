package com.code.truck.finances.app.core.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String username;

    public User(UUID id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public static User create(String email, String username) {
        return new User(UUID.randomUUID(), email, username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
