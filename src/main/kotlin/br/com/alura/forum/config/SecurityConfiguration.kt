package br.com.alura.forum.config

import br.com.alura.forum.security.JWTAuthenticationFilter
import br.com.alura.forum.security.JWTLoginFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JWTUtil,
    private val configuration: AuthenticationConfiguration
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/swagger-ui/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                    .requestMatchers("/topicos")?.hasAuthority("LEITURA_ESCRITA")
                    ?.anyRequest()?.authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(
                JWTLoginFilter(authManager = configuration.authenticationManager, jwtUtil = jwtUtil),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                JWTAuthenticationFilter(jwtUtil = jwtUtil),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .csrf { it.disable() } // por padrão o Spring Security protege contra (POST/PUT/DELETE) tem que usar dessa forma para desabilitar
//            .authorizeHttpRequests { it.anyRequest().authenticated() }
//            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
//            .addFilterBefore(
//                JWTLoginFilter(authManager = configuration.authenticationManager, jwtUtil = jwtUtil),
//                UsernamePasswordAuthenticationFilter::class.java
//            )
//            .addFilterBefore(
//                JWTAuthenticationFilter(jwtUtil = jwtUtil),
//                UsernamePasswordAuthenticationFilter::class.java
//            )
//            .authorizeHttpRequests {
//                it.requestMatchers("/error").permitAll()
//                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
//                    .anyRequest().authenticated()
//            }
////            .authorizeHttpRequests { auth ->
////                auth
////                    //.requestMatchers("/topicos").hasAuthority("LEITURA_ESCRITA")
////                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
////                    .anyRequest().authenticated()
////                    .addFilterBefore(JWTLoginFilter(authManager = configuration.authenticationManager, jwtUtil = jwtUtil), UsernamePasswordAuthenticationFilter().javaClass)
////                    http.addFilterBefore(JWTAuthenticationFilter(jwtUtil = jwtUtil), OncePerRequestFilter::class.java)
////            }
//
////            http.sessionManagement { session ->
////                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // não guarda o estado
////            }
//            //.formLogin { it.disable() } // desabilita o formulário do login
//            //.httpBasic { }
//
//        return http.build()
//    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder() // Criptografa a senha
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}