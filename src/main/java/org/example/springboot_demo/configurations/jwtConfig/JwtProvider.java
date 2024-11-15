package org.example.springboot_demo.configurations.jwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String JWT_SECRET;
    @Value("${jwt.expiration}")
    private int JWT_EXPIRATION;

    private final EmployeeService employeeService;

    @Autowired
    public JwtProvider(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public String generateTokenByUsername(String Username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        EmployeeDto employeeDto = employeeService.findByUsername(Username);
        return Jwts.builder()
                .setSubject(Long.toString(employeeDto.getEmployeeId()))
                .claim("username", employeeDto.getUsername())
                .claim("role", employeeDto.getRoleId())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getKeyByValueFromJWT(String key, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key, String.class);
    }
}
