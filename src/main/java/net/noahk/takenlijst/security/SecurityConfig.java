package net.noahk.takenlijst.security;

import net.noahk.takenlijst.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig  {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SecurityConfig(JwtService service, UserRepository userRepos) {
        this.jwtService = service;
        this.userRepository = userRepos;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder encoder, UserDetailsService udService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(udService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService(this.userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/users").permitAll()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                .antMatchers(HttpMethod.GET, "/projects").hasAnyAuthority("TEAM_MEMBER")
                .antMatchers(HttpMethod.GET, "/projects/**").hasAnyAuthority("TEAM_MEMBER")
                .antMatchers(HttpMethod.GET, "/attachments/**").permitAll()
                .antMatchers("/projects").hasAnyAuthority("TEAM_LEADER")
                .antMatchers("/projects/**").hasAnyAuthority("TEAM_LEADER")
                .antMatchers("/tasks").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/tasks/**").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/comments").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/comments/**").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/attachments").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/attachments/**").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/points").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/points/**").hasAnyAuthority("TEAM_MEMBER", "TEAM_LEADER")
                .antMatchers("/**").hasAuthority("ADMIN")
                .and()
                .addFilterBefore(new JwtRequestFilter(jwtService, userDetailsService()), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
