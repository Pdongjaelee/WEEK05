package com.sparta.post03.jwt.provider;


import com.sparta.post03.dto.TokenDto;
import com.sparta.post03.entity.Member;
import com.sparta.post03.security.UserDetailImp;
import com.sparta.post03.security.UserDetailServiceImp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    private static final String SECRET = "SPARTA@1242351231231245352346#$%";

    private final Key key;

    private final UserDetailServiceImp userDetailServiceImp;

    public JwtProvider(UserDetailServiceImp userDetailServiceImp) {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
        this.userDetailServiceImp = userDetailServiceImp;
    }

    // 토큰만들기
        public TokenDto generateTokenDto(Authentication authentication) {

            long now = (new Date()).getTime();

            Date accessTokenExpIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
            // Access Token 생성
            String accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .setExpiration(accessTokenExpIn)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Refresh Token 생성
            String refreshToken = Jwts.builder()
                    .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return new TokenDto(BEARER_TYPE, accessToken, refreshToken, accessTokenExpIn.getTime());
    }

    // 권한정보받기
        public Authentication getAuthentication(String token) {
            Claims claims = parseClaims(token);
            UserDetails userDetails = userDetailServiceImp.loadUserByUsername(claims.getSubject());
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }

        private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Member getMemberFromAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())){
            return null;
        }
        return((UserDetailImp) authentication.getPrincipal()).getMember();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 헤더에서 토큰을 가져와 앞7자리 "bearer "를 때낸 뒤 토큰값을 반환
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

