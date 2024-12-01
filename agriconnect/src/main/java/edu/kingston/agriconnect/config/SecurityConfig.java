package edu.kingston.agriconnect.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/**", "/**").permitAll()
                        .anyRequest().authenticated()
                );
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults()); //? for basic auth - Rest API Access
        return http.build();

    }
}


//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/farmer/**").hasRole("FARMER")
//                .antMatchers("/buyer/**").hasRole("BUYER")
//                .antMatchers("/login").permitAll()
//                .anyRequest().authenticated();
//    }
//}