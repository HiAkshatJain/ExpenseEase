package authservice.auth;

import authservice.service.JwtService;
import authservice.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that processes JWT-based authentication.
 * This filter is invoked once per request to validate the JWT and set up the security context.
 */
@Component
@AllArgsConstructor
@Data
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;  // Service to handle JWT operations

    @Autowired
    private final UserDetailsServiceImpl userDetailsService;  // Service to load user details

    /**
     * This method is called for every HTTP request to validate the JWT token and set the authentication context.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the 'Authorization' header from the request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if the header contains a JWT token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Remove the 'Bearer ' prefix to get the token
            token = authHeader.substring(7);
            // Extract the username from the token
            username = jwtService.extractUsername(token);
        }

        // Check if the username is not null and no authentication is currently set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token with the user details
            if (jwtService.validateToken(token, userDetails)) {
                // Create an authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Set additional details for the authentication token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the authentication token in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue with the next filter in the chain or the target servlet
        filterChain.doFilter(request, response);
    }
}
