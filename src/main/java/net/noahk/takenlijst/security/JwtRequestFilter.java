package net.noahk.takenlijst.security;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    public JwtRequestFilter(JwtService jwtService, UserDetailsService udService) {
        this.jwtService = jwtService;
        this.userDetailsService = udService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest
                                            request,
                                    @NonNull HttpServletResponse
                                            response,
                                    @NonNull FilterChain
                                            filterChain) throws ServletException, IOException {
        final var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authorizationHeader.substring(7);
        var username = jwtService.extractUsername(jwt);
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            // Already authenticated or no username in token means we don't need to set authentication.
            filterChain.doFilter(request, response);
            return;
        }

        var userDetails = this.userDetailsService.loadUserByUsername(username);
        if (!jwtService.validateToken(jwt, userDetails)) {
            // Invalid token
            filterChain.doFilter(request, response);
            return;
        }

        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
        );
        var details = new WebAuthenticationDetailsSource().buildDetails(request);
        usernamePasswordAuthenticationToken.setDetails(details);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }
}
