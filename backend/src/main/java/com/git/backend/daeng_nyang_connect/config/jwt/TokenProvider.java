package com.git.backend.daeng_nyang_connect.config.jwt;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.CustomerUserDetails;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import com.git.backend.daeng_nyang_connect.user.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    @Value("${security.jwt.secret}")
    private String secretKey;
    private long accesstokenValidSecond = 500L * 60 * 60; //30분
    private long refreshtokenValidSecond = 1000L * 60 * 60; //1시간

    private final CustomerUserDetails customerUserDetails;
    private final UserRepository userRepository;

    @PostConstruct
    protected void init(){
        secretKey  = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private String getString(String email, long accesstokenValidSecond, Role role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        Date accessValidate = new Date(now.getTime() + accesstokenValidSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setExpiration(accessValidate)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    //액세스 토큰 생성
    public String createAccessToken(String email){
        Optional<User> isUser = userRepository.findByEmail(email);
        return isUser.map(user -> getString(email, accesstokenValidSecond, user.getRole()))
                .orElseThrow(() -> new NoSuchElementException("없는 회원 입니다"));
    }
    //리프레쉬 토큰 생성
    public String createRefreshToken(String email){
        Optional<User> isUser = userRepository.findByEmail(email);
        return isUser.map(user -> getString(email, refreshtokenValidSecond, user.getRole()))
                .orElseThrow(() -> new NoSuchElementException("없는 회원 입니다"));
    }

    private String getUserEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = customerUserDetails.loadUserByUsername(this.getUserEmail(token));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "", userDetails.getAuthorities());
        log.info("Authentication authorities: " + authentication.getAuthorities());
        return authentication;
    }

    //토큰 값 가져오기
    public String resolveToken(HttpServletRequest request){
        String token = request.getHeader("access_token");
        log.info("Resolved token: " + token);
        return token;
    }

    //유효성 검증
    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();
            Date now = new Date();
            boolean isValid = claims.getExpiration().after(now);
            log.info("Token validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰에서 이메일 값 추출
    public String getEmailBytoken(String token) {

        // JWT 토큰을 디코딩하여 페이로드를 얻기
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        // "userId" 클레임의 값을 얻기
        return claims.isEmpty() ? null : claims.get("sub", String.class);
    }




}
