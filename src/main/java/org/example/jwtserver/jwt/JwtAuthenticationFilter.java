package org.example.jwtserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwtserver.auth.PrincipalDetails;
import org.example.jwtserver.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter
// /login 요청해서 username, password 전송하면(post)
// UsernamePasswordAuthenticationFilter 동작

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter : 로그인 시도함");

        // 1. username, password 받아서
        try {
            // 클라이언트에서 전송한 사용자 정보를 객체로 변환
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            log.info(String.valueOf(user));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            //PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨
            // DB에 있는 usernamer과 password가 일치한다.
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info(principalDetails.getUsername()); // 로그인이 정상적으로 되었다는 뜻

            //authentication 객체가 session 영역에 저장을 하야하고 return 해줌
            // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없지만, 권한 처리 때문에 session을 넣어줌
            return authentication;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 2. 정상인지 로그인 시도 해보기 authenticationManager로 로그인 시도 하면
        // PrincipalDetailsService가 호출 loadUserByUsername() 실행

        // 3. PrincipalDetails를 세션에 담고 (권한 관리를 위해서)

        // 4. JWT 토큰을 만들어서 응답해주면 됨

    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication ");
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
