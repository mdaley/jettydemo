package com.sequsoft.jettydemo;

import com.sequsoft.jettydemo.jwt.JwtAuthenticationProvider;
import com.sequsoft.jettydemo.jwt.JwtTokenFilter;
import com.sequsoft.jettydemo.plugins.JwtPublicKeyPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final JwtPublicKeyPlugin jwtPublicKeyPlugin;

    @Autowired
    public SecurityConfig(JwtPublicKeyPlugin jwtPublicKeyPlugin) {
        this.jwtPublicKeyPlugin = jwtPublicKeyPlugin;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider());
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint((req, resp, ex) ->
                        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())).and()
                .authorizeRequests().anyRequest().permitAll().and()
                .addFilterAfter(jwtTokenFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    JwtTokenFilter jwtTokenFilter() {
        try {
            return new JwtTokenFilter(authenticationManager());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtPublicKeyPlugin);
    }
}
