package org.example.jwtserver.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .addFilter(corsFilter)  //인증 있으면 시큐리티 필터에 등록
            .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 시 세션 사용 안 함
            .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/api/v1/user/**").authenticated() // 인증된 사용자만 접근 가능
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // ADMIN 권한이 있는 사용자만 접근 가능
                            .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER") // ADMIN 또는 MANAGER 권한이 있는 사용자만 접근 가능
                            .anyRequest().permitAll() // 나머지 요청은 모두 허용
            );
        return httpSecurity.build();
    }
}
