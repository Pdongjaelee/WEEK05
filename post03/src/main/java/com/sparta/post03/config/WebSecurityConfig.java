package com.sparta.post03.config;
import com.sparta.post03.jwt.filter.JwtFilter;
import com.sparta.post03.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private final JwtProvider jwtProvider;

    //암호화 하지않으면 security에서 로그인을 막음
    @Bean
    public BCryptPasswordEncoder encodePassword() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //예를 들어 A라는 도메인에서, 인증된 사용자 H가 위조된 request를 포함한 link, email을 사용하였을 경우(클릭, 또는 사이트 방문만으로도),
        // A 도메인에서는 이 사용자가 일반 유저인지, 악용된 공격인지 구분할 수가 없다.
        //CSRF protection은 spring security에서 default로 설정된다.
        //그래서 이렇게 보안 수준을 향상시키는 CSRF를 왜 disable 하였을까?
        // spring security documentation에 non-browser clients 만을 위한 서비스라면 csrf를 disable 하여도 좋다고 한다.
        //이 이유는 rest api를 이용한 서버라면, session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관하지 않는다.
        // rest api에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 한다.
        // 따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.

        /*
        * jwt토큰을 이용해서 인증하는 경우 stateless방식을 사용하기 때문에 default 값인 csrf기능을 사용할 필요가 없다.
        */
        // CSRF 설정 Disable
        http.csrf().disable();

        http
                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                // 아래의 경로로 usl 요청이 들어오면 인증된 user만 진입 허용
                .antMatchers("/api/auth/posts/**").permitAll()
                ///api/auth/comment
                //api/auth/recomment
                // 나머지 request는 인증없이 허용
                .anyRequest().permitAll()
                .and()
                //JWT 토큰 필터 처리하는 부분
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
    }
}