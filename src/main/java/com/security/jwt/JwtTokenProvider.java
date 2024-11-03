package com.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.security.security.UserPrincipal;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "wR533pkHvoMk";

    private long jwtExpiration = 86400000;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }


    public String generateToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        try {

            return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withClaim("roles", userPrincipal.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()))
                .withClaim("username", userPrincipal.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(getAlgorithm())
            ;

        } catch(JWTCreationException exception) {
            throw new JWTCreationException("Erro ao gerar o token", exception);
        }

    }
    
    public String getUsernameFromJWT(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
        .build()
        .verify(token);

        return decodedJWT.getSubject();
    }

    public boolean validateToken (String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm()).build();
            verifier.verify(token);
            return true;
        } catch(JWTVerificationException exception) {
            return false;
        }
    }
}