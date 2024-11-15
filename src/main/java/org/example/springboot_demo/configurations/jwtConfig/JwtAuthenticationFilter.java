package org.example.springboot_demo.configurations.jwtConfig;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_demo.configurations.UserDetailsImpl;
import org.example.springboot_demo.repositories.IEmployeeRepository;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final EmployeeService employeeService;
    private final IEmployeeRepository userRepository;
    @Value("${jwt.secret-key}")
    private String JWT_SECRET;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider jwtProvider, EmployeeService employeeService, IEmployeeRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.employeeService = employeeService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwtToken = getJwtFromRequest(request);
            if(jwtToken != null && this.validateToken(jwtToken)){
                String username = jwtProvider.getKeyByValueFromJWT("username", jwtToken);
                UserDetailsImpl userDetails = (UserDetailsImpl) employeeService.loadUserByUsername(username);
                if(userDetails != null) {
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String authToken){
        try{
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e){
            logger.error("Invalid JWT signature: "+ e.getMessage());
        }
        catch (MalformedJwtException e){
            logger.error("Invalid JWT token: "+ e.getMessage());
        }
        catch (ExpiredJwtException e){
            logger.error("JWT token is expired: "+ e.getMessage());
        }
        catch (UnsupportedJwtException e){
            logger.error("JWT token unsupported: "+ e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: "+ e.getMessage());
        }
        return false;
    }
}
