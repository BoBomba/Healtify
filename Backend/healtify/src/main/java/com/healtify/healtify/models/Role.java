package com.healtify.healtify.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserAccount> users = new java.util.HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public Set<UserAccount> getUsers() {
    //     return users;
    // }

    public Set<UserAccount> getUsers() {
    if (users == null) {
        users = new HashSet<>();
    }
    return users;
}

    public void setUsers(Set<UserAccount> users) {
        this.users = users;
    }

    public Role() {
    }

    public Role(Integer id, String name, Set<UserAccount> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.users = builder.users;
    }

    public static class Builder {
        private Integer id;
        private String name;
        private Set<UserAccount> users;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder users(Set<UserAccount> users) {
            this.users = users;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}
