package com.agilysis.onboarding.auth;

import com.agilysis.onboarding.model.AdminUser;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String secret_key = "8T0rtdn6Dg";

    private final JwtParser jwtParser;

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createTokenFromClaims(Claims claims) {
        Date tokenCreateTime = new Date();
        long accessTokenValidity = 60 * 60 * 1000;
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    public String createToken(AdminUser user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail()).setIssuer("issuer").setAudience("audience");
        return createTokenFromClaims(claims);
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.setSigningKey(secret_key).parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String TOKEN_HEADER = "Authorization";
        String TOKEN_PREFIX = "Bearer ";
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(String originalToken, Claims claims) throws AuthenticationException {
        if (!"issuer".equals(claims.getIssuer()) || !"audience".equals(claims.getAudience())) {
            throw new JwtException("Invalid issuer or audience");
        }

        String[] tokenParts = originalToken.split("\\.");
        String originalSignature = tokenParts[2];

        String recreatedToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();

        String[] recreatedTokenParts = recreatedToken.split("\\.");
        String recreatedSignature = recreatedTokenParts[2];

        if (!originalSignature.equals(recreatedSignature)) {
            throw new JwtException("Invalid token");
        }

        return claims.getExpiration().after(new Date());
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }


}
