package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http.authorizeHttpRequests((authz) -> {
                    try {
                        authz.anyRequest().authenticated()
                              .and()
                              .formLogin(formLogin -> formLogin
		                              .loginPage("/login")
		                              .permitAll()
                            		  );  
                        
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    
                });
    	
        
        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
//    	ignores theses things from the spring security, so could 
//    	be displayed / fetched without logging in.
    	return ((web) -> web.ignoring()
    						.requestMatchers("/images/**", "/js/**", "/webjars/**")
    			);
    }

}
