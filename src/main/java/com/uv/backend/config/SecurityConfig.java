package com.uv.backend.config;

import com.uv.backend.security.JwtAuthenticationEntryPoint;
import com.uv.backend.security.JwtAuthenticationFilter;
import com.uv.backend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Media and file uploads endpoints
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/media/**").permitAll()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()

                        // Audio streaming endpoints
                        .requestMatchers("/stream/**").permitAll()
                        .requestMatchers("/audio/**").permitAll()

                        // Image serving endpoints
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/avatars/**").permitAll()
                        .requestMatchers("/covers/**").permitAll()
                        .requestMatchers("/thumbnails/**").permitAll()

                        // Waveform endpoints
                        .requestMatchers("/waveforms/**").permitAll()

                        // Public read endpoints
                        .requestMatchers(HttpMethod.GET, "/api/tracks/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/playlists/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/media/public/**").permitAll()

                        // Public media download endpoints
                        .requestMatchers(HttpMethod.GET, "/api/media/download/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()

                        // Protected media upload endpoints
                        .requestMatchers(HttpMethod.POST, "/api/media/upload/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/media/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/files/**").authenticated()

                        // Protected endpoints
                        .requestMatchers("/api/tracks/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/posts/**").authenticated()
                        .requestMatchers("/api/comments/**").authenticated()
                        .requestMatchers("/api/playlists/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/media/**").authenticated()
                        .requestMatchers("/api/files/**").authenticated()

                        .anyRequest().authenticated()
                );

        // H2 Console fix
        http.headers().frameOptions().disable();

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Range", "X-Content-Range"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}