package com.project.community.community_security_service.config;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF (not needed for stateless JWT)
//                .csrf(csrf -> csrf.disable())
//
//                // Configure endpoint authorization
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers(HttpMethod.POST,"/auth/register").permitAll()
//
//                        // All other endpoints require authentication
//                        .anyRequest().authenticated()
//                )
//
//                // Stateless session (required for JWT)
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}
