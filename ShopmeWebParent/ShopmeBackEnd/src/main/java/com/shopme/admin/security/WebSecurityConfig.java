package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Bean
	UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // Allows no-password login
//        http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
//        
//        return http.build();
//    }
    
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    	authProvider.setUserDetailsService(userDetailsService());
    	authProvider.setPasswordEncoder(passwordEncoder());
    	
    	return authProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	http.authenticationProvider(authenticationProvider());
    	
    	http.authorizeHttpRequests(auth -> auth
    								.anyRequest()
    								.authenticated()
                              )
                              .formLogin(formLogin -> formLogin
		                              .loginPage("/login")
		                              .usernameParameter("email")
		                              .permitAll()
                            		  );      	
        
        return http.build();
    }
    
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
    	// ignores theses things from the spring security, so could 
    	// be displayed / fetched without logging in.
    	return ((web) -> web.ignoring()
    						.requestMatchers("/images/**", "/js/**", "/webjars/**")
    			);
    }

}
