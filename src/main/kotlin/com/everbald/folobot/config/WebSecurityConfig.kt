package com.everbald.folobot.config

import com.everbald.folobot.model.Authority
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/admin/**", hasAuthority(Authority.ROLE_ADMIN.name))
                authorize("/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            csrf {
                ignoringRequestMatchers("/telegram-hook")
            }
            formLogin {
                loginPage = "/login"
                permitAll = true
                failureUrl = "/login?error=true"
            }
            logout {
                permitAll = true
                deleteCookies("JSESSIONID")
            }
            rememberMe {
                key = "rm-key"
            }
        }
        return http.build()
    }

    @Bean
    fun getUserDetailsManager(dataSource: DataSource): UserDetailsManager {
        return JdbcUserDetailsManager(dataSource)
    }

    @Bean
    fun getPasswordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }
}