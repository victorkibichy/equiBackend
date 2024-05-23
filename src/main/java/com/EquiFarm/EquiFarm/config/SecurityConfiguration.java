package com.EquiFarm.EquiFarm.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  @Value("${application.client.origin}")
  private String clientOrigin;

  @Bean
  CorsConfigurationSource corsConfigurationSource()
  {
  CorsConfiguration config = new CorsConfiguration();
  config.setAllowCredentials(false);
  // For Security Set to only allow request from a static elastic IP
  config.addAllowedOrigin(clientOrigin);
  config.addAllowedOrigin("**");
  config.addAllowedHeader("*");
  config.addAllowedMethod("OPTIONS");
  config.addAllowedMethod("HEAD");
  config.addAllowedMethod("GET");
  config.addAllowedMethod("PUT");
  config.addAllowedMethod("POST");
  config.addAllowedMethod("DELETE");
  config.addAllowedMethod("PATCH");
  UrlBasedCorsConfigurationSource source = new
  UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/**", config);
  return source;
  
  }
  



  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
   
    http.cors(cors -> cors.disable());
    http.csrf(csrf -> csrf.disable());
//   http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
        .requestMatchers(
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html")
        .permitAll() 
        .anyRequest().permitAll())
        // .authenticated() 
        ;

    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}



 // http.cors().and().csrf()
    // .disable().

    // authorizeHttpRequests()

    // .requestMatchers(
    // "/api/v1/auth/**",
    // "/v2/api-docs",
    // "/v3/api-docs",
    // "/v3/api-docs/**",
    // "/swagger-resources",
    // "/swagger-resources/**",
    // "/configuration/ui",
    // "/configuration/security",
    // "/swagger-ui/",
    // "/swagger-ui/**",
    // "/webjars/**",
    // "/swagger-ui.html",
    // "/swagger-ui/index.html"

    // )
    // .permitAll()

    // .requestMatchers("/api/v1/**")
    // .hasAnyRole(ADMIN.name())
    // .requestMatchers(POST, "/api/v1/**")
    // .hasAnyAuthority(ADMIN_CREATE.name())

    // .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name())

    // .requestMatchers(GET,
    // "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name())
    // .requestMatchers(POST,
    // "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name())
    // .requestMatchers(PUT,
    // "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name())
    // .requestMatchers(DELETE,
    // "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name())

    // .anyRequest().permitAll()

    // // .authenticated()
    // .and()
    // .sessionManagement()
    // .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    // .and()
    // .authenticationProvider(authenticationProvider)
    // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    // .logout()
    // .logoutUrl("/api/v1/auth/logout")
    // .addLogoutHandler(logoutHandler)
    // .logoutSuccessHandler((request, response, authentication) ->
    // SecurityContextHolder.clearContext())
    // ;