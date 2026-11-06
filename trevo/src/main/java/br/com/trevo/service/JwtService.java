package br.com.trevo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerar(Integer usuarioId) {
        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    public Integer extrairUsuarioId(String token) {
        String subject = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Integer.parseInt(subject);
    }

    public String gerarReset(Integer usuarioId) {
        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("tipo", "reset")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900_000))  // 15 min
                .signWith(getKey())
                .compact();
    }

    public Integer extrairUsuarioIdReset(String token) {
        var claims = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload();

        if (!"reset".equals(claims.get("tipo"))) {
            throw new RuntimeException("Token inválido");
        }
        return Integer.parseInt(claims.getSubject());
    }
}
