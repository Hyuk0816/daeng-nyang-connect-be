package com.git.backend.daeng_nyang_connect.user.service;


import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.user.dto.LoginDto;
import com.git.backend.daeng_nyang_connect.user.dto.SignUpDto;
import com.git.backend.daeng_nyang_connect.user.entity.MyPage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import com.git.backend.daeng_nyang_connect.user.role.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String ,String > redisTemplate;

//    //회원 가입 시 userID 자동으로 my page에 저장
//    근데 그냥 my page 작성 시 토큰으로 user id 찾기해서 저장하는 방법도 있어서 보류
//    public MyPage myPageEntity(User user){
//        return MyPage.builder()
//                .user(user)
//                .build();
//    }
    @Transactional
    public ResponseEntity<?>signUp(SignUpDto signUpDto){
        String email = signUpDto.getEmail();
        String password = signUpDto.getPassword();
        String name = signUpDto.getName();
        String nickName = signUpDto.getNickname();
        String mobile = signUpDto.getMobile();
        String city = signUpDto.getCity();
        String town = signUpDto.getTown();
        boolean experience = signUpDto.isExperience();
        char gender = signUpDto.getGender();

        if(userRepository.existsByEmail(email)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이미 사용 중인 아이디 입니다");
        }

        if(userRepository.existsByNickname(nickName)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이미 사용 중인 닉네임 입니다");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickName)
                .mobile(mobile)
                .city(city)
                .town(town)
                .experience(experience)
                .gender(String.valueOf(gender))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입이 되었습니다");

    }

    @Transactional
    public Map<String, String> login(LoginDto loginDto, HttpServletResponse httpServletResponse) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 회원이 없을 경우 예외 처리
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 없습니다"));

            // 로그아웃 토큰이 있는 경우 삭제
            if (redisTemplate.opsForValue().get("logout: " + loginDto.getEmail()) != null) {
                redisTemplate.delete("logout: " + loginDto.getEmail());
            }

            String accessToken = tokenProvider.createAccessToken(email);
            String refreshToken = tokenProvider.createRefreshToken(email);

            redisTemplate.opsForValue().set(email, accessToken, Duration.ofSeconds(1800));
            redisTemplate.opsForValue().set("RF: " + email, refreshToken, Duration.ofHours(1L));

            httpServletResponse.addCookie(new Cookie("access_token", accessToken));
            httpServletResponse.addCookie(new Cookie("refresh_token", refreshToken));

            Map<String, String> response = new HashMap<>();
            response.put("message", "로그인 되었습니다");
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("http_status", HttpStatus.OK.toString());
            return response;

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "잘못된 자격 증명입니다");
            response.put("http_status", HttpStatus.UNAUTHORIZED.toString());
            return response;


        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "가입되지 않은 회원입니다");
            response.put("http_status", HttpStatus.NOT_FOUND.toString());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "알 수 없는 오류가 발생했습니다");
            response.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return response;
        }
    }






}
