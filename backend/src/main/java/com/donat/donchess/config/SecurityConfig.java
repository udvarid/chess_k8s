package com.donat.donchess.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.savedrequest.NullRequestCache;


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Lazy
	private UserDetailsService userService;

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	@Override
	protected void configure(HttpSecurity httpSec) throws Exception {
		httpSec
			.csrf().ignoringAntMatchers("/**").and()
			.authorizeRequests().antMatchers(HttpMethod.GET, "/**").permitAll()
			.antMatchers(HttpMethod.POST, "/api/user/login/**").permitAll()
			.antMatchers(HttpMethod.POST, "/api/user/register/**").permitAll()
			.antMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "USER")
			.antMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "USER")
			.antMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN", "USER")
			.and().sessionManagement().sessionFixation().migrateSession()
			.and().requestCache().requestCache(new NullRequestCache()).and()
			.cors().and()
			.headers().frameOptions().disable()
			.and().formLogin().permitAll();
	}


}
