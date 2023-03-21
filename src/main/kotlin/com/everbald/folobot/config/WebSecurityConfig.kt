package com.everbald.folobot.config

import com.everbald.folobot.model.Authority
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val dataSource: DataSource
) {
    @Bean
    fun getFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests()
                .requestMatchers("/admin/**").hasAuthority(Authority.ROLE_ADMIN.name)
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .csrf()
                .ignoringRequestMatchers("/telegram-hook")
            .and()
                .formLogin().loginPage("/login").permitAll()
                .failureUrl("/login?error=true")
            .and()
                .logout().permitAll().deleteCookies("JSESSIONID")
            .and()
                .rememberMe().key("rm-key");
       return http.build()
    }

    @Bean
    fun getUserDetailsManager(dataSource: DataSource): UserDetailsManager {
        return JdbcUserDetailsManager(dataSource);
    }

    @Bean
    fun getPasswordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }
}