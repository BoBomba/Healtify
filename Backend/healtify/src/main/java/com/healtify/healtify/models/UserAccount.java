package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healtify.healtify.security.service.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_account")
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Getters and setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Role musza wyjsc na zewnatrz jako GrantedAuthority, inaczej hasRole()/@PreAuthorize
    // nie widzi nic i kazdy chroniony endpoint konczy sie 403 (wczesniej bylo tu List.of()).
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .filter(name -> name != null)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /** Skrot uzywany w kontrolerach - nazwy rol sa trzymane z prefiksem ROLE_ (patrz RoleEnum). */
    @JsonIgnore
    public boolean hasRole(RoleEnum role) {
        return roles != null && roles.stream().anyMatch(r -> role.name().equals(r.getName()));
    }

    // Hash hasla nigdy nie moze wyjsc w JSON-ie (endpointy /api/data/user i /api/data/settings
    // zwracaja cala encje) - Jackson pomija to pole przy serializacji.
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Builder {
        private String username;
        private String email;
        private Set<Role> roles = new HashSet<>();
        private String password;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserAccount build() {
            UserAccount user = new UserAccount();
            user.username = this.username;
            user.email = this.email;
            user.roles = this.roles;
            user.password = this.password;
            return user;
        }
    }

    // constructors

    public UserAccount() {
    }

    public UserAccount(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
