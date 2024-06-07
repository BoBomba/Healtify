package com.healtify.healtify.security.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value(value = "${application.security.jwt.secret-key}")
    private String secretKey;

//  generate RSA key pair public key copy here and private key add to for example application.yml file and keep it secret and safe
//    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArns1cvlrog7zcurKqZjdxDI2+9zFufmbFJt9CLfYAlx1LKdmwkhpm1UR1gkh8bmOPqfiXVYA8ZzM8OtDSO+WySuJUET0bEuorVIhkAafIYJ91pu3yUTfAa1tk5r4Eecsab82qpimriumfseehu9QzYjzWqZVExu2TH/vz42B1w7/9irEjG40VGO1WwWWci+VT7abT1SAQuIefeaaBF6U7pyRrCncMcTPNhhkV/QW/AiTr951vMR3PYnRYpLxcPBVKNPWoIBEDG6ayllauavsJvSBmkR+4P8UkaN3ZxeDmLIqrTGzb9gzpgFCio9JYAGEVKYCJ9cU72DOEFaaT+V5AQIDAQAB";

// public key not used in this project


    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
//                .setSigningKey(getSignInPublicKey())
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    private Key getSignInPublicKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(publicKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}