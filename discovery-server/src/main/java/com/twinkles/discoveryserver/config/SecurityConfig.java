package com.twinkles.discoveryserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private final String username = System.getenv("USER_NAME");
    private final String password = System.getenv("PASSWORD");
    @Override
    public void configure(AuthenticationManagerBuilder  authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.inMemoryAuthentication().
                passwordEncoder(NoOpPasswordEncoder.getInstance()).
                withUser(username).password(password).
                authorities("USER");
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
