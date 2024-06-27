package com.swd392.group2.kgrill_service.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.swd392.group2.kgrill_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtImplement implements JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration.access-token}")
    private long jwtExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshExpiration;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Override
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    @Override
    public String generateEncryptedToken(Map<String, Object> claims, UserDetails userDetails) throws JOSEException {
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issuer(jwtIssuer)
                .audience(jwtIssuer)
                .claim("role", populateAuthorities(userDetails.getAuthorities()))
                .claim("type", "Bearer")
                .expirationTime(new Date(System.currentTimeMillis() + jwtExpiration))
                .issueTime(new Date());
        claims.forEach(claimsSetBuilder::claim);
        JWTClaimsSet claimsSet = claimsSetBuilder.build();
        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                .contentType("JWT")
                .build();
        EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);
        byte[] encryptionKeyBytes = Decoders.BASE64.decode(secretKey);
        DirectEncrypter encrypter = new DirectEncrypter(encryptionKeyBytes);
        encryptedJWT.encrypt(encrypter);
        return encryptedJWT.serialize();
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");

        return Jwts.builder()
                .header().add(headers).and()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("role", populateAuthorities(userDetails.getAuthorities()))
                .claim("type", "Bearer")
                .issuer(jwtIssuer)
                .signWith(getSignInKey())
                .compact();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    @Override
    public JWTClaimsSet decryptJwt(String encryptedToken) throws ParseException, JOSEException {
        byte[] encryptionKeyBytes = Decoders.BASE64.decode(secretKey);
        EncryptedJWT encryptedJWT = EncryptedJWT.parse(encryptedToken);
        encryptedJWT.decrypt(new DirectDecrypter(encryptionKeyBytes));
        return encryptedJWT.getJWTClaimsSet();
    }

    public boolean isEncryptedTokenValid(JWTClaimsSet claims, UserDetails userDetails) {
        final String username = claims.getSubject();
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(String.valueOf(claims)));
    }

}
