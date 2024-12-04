package utez.edu._b.sgc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/verify-code").permitAll()
                        .requestMatchers("/user/send-email").permitAll()

                        .requestMatchers("/user/change-pass").permitAll()

                        .requestMatchers("/user/findId/**").permitAll()// hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")


                        .requestMatchers("/customers/**").permitAll()//.hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/projectCat/**").permitAll()//.hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/projects/**").permitAll()//.hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/user/**").permitAll()//.hasAuthority("ROLE_ADMIN")

                        /*
                        // Rutas accesibles por ROLE_USER para consultas
                        .requestMatchers("/customers/all").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/customers/active").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/projectCat/all").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/projectCat/active").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/projects/active").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/projects/all").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/user/active").permitAll()//.hasAuthority("ROLE_USER")
                        .requestMatchers("/user/all").permitAll()


                        */

                        .anyRequest().permitAll()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}