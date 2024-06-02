package com.healtify.healtify.security.token;

import jakarta.persistence.*;
import com.healtify.healtify.security.token.TokenType;
import com.healtify.healtify.models.UserAccount;

@Entity
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserAccount userAccount;

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Token() {
    }

    public Token(String token, TokenType tokenType, boolean revoked, boolean expired, UserAccount userAccount) {
        this.token = token;
        this.tokenType = tokenType;
        this.revoked = revoked;
        this.expired = expired;
        this.userAccount = userAccount;
    }

    private Token(Builder builder) {
        this.token = builder.token;
        this.tokenType = builder.tokenType;
        this.revoked = builder.revoked;
        this.expired = builder.expired;
        this.userAccount = builder.userAccount;
    }

    public static class Builder {
        private String token;
        private TokenType tokenType;
        private boolean revoked;
        private boolean expired;
        private UserAccount userAccount;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder tokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder expired(boolean expired) {
            this.expired = expired;
            return this;
        }

        public Builder userAccount(UserAccount userAccount) {
            this.userAccount = userAccount;
            return this;
        }

        public Token build() {
            return new Token(this);
        }
    }
}
