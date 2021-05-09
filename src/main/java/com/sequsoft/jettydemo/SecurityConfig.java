package com.sequsoft.jettydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final JwtPublicKeyProvider jwtPublicKeyProvider;

    @Autowired
    public SecurityConfig(JwtPublicKeyProvider jwtPublicKeyProvider) {
        this.jwtPublicKeyProvider = jwtPublicKeyProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint((req, resp, ex) -> {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                }).and()
                .authorizeRequests().anyRequest().permitAll().and()
                .addFilterAfter(jwtTokenFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    JwtUtils jwtUtils() {
        return new JwtUtils(jwtPublicKeyProvider);
    }

    @Bean
    JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtUtils());
    }
}
