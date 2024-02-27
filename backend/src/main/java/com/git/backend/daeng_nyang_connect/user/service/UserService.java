package com.git.backend.daeng_nyang_connect.user.service;


import com.amazonaws.services.kms.model.NotFoundException;
import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.config.security.SecurityConfig;
import com.git.backend.daeng_nyang_connect.user.dto.FindDto;
import com.git.backend.daeng_nyang_connect.user.dto.LoginDto;
import com.git.backend.daeng_nyang_connect.user.dto.SignUpDto;
import com.git.backend.daeng_nyang_connect.user.entity.MyPage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.MyPageRepository;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import com.git.backend.daeng_nyang_connect.user.role.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String ,String > redisTemplate;
    private final MyPageRepository myPageRepository;
    private final SecurityConfig securityConfig;


    @Value("${basicProfile}")
    private String basicProfile;


//    //회원 가입 시 userID 자동으로 my page에 저장
//    근데 그냥 my page 작성 시 토큰으로 user id 찾기해서 저장하는 방법도 있어서 보류
    public MyPage myPageEntity(User user){
        return MyPage.builder()
                .user(user)
                .img(basicProfile)
                .build();
    }

    @Transactional
    public ResponseEntity<?>signUp(SignUpDto signUpDto){
        String email = signUpDto.getEmail();
        String password = signUpDto.getPassword();
        String name = signUpDto.getName();
        String nickname = signUpDto.getNickname();
        String mobile = signUpDto.getMobile();
        String city = signUpDto.getCity();
        String town = signUpDto.getTown();
        boolean experience = signUpDto.isExperience();
        char gender = signUpDto.getGender();

        if(userRepository.existsByEmail(email)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이미 사용 중인 아이디 입니다");
        }

        if(userRepository.existsByNickname(nickname)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이미 사용 중인 닉네임 입니다");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .mobile(mobile)
                .city(city)
                .town(town)
                .experience(experience)
                .gender(gender)
                .role(Role.USER)
                .build();

        userRepository.save(user);


        MyPage myPage = myPageEntity(user);
        myPageRepository.save(myPage);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입이 되었습니다");

    }

    //로그인
    @Transactional
    public ResponseEntity<?> login(LoginDto loginDto, HttpServletResponse httpServletResponse) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 회원이 없을 경우 예외 처리
            User isUser = userRepository.findByEmail(email)
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
            response.put("nickname", isUser.getNickname());
            response.put("id", isUser.getUserId().toString());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "잘못된 자격 증명입니다");
            response.put("http_status", HttpStatus.UNAUTHORIZED.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);


        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "가입되지 않은 회원입니다");
            response.put("http_status", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "알 수 없는 오류가 발생했습니다");
            response.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);

        }
    }

    //로그아웃
    @Transactional
    public void logout(String token){
        redisTemplate.opsForValue().set("logout : "+ tokenProvider.getEmailBytoken(token), "logout", Duration.ofSeconds(1800));
        redisTemplate.delete(tokenProvider.getEmailBytoken(token));
        redisTemplate.delete("RF: " + tokenProvider.getEmailBytoken(token));

    }

    //토큰으로 user 를 체크
    public User checkUserByToken(String token){
        String email = tokenProvider.getEmailBytoken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("없는 유저 입니다"));
    }
    //아이디 중복 체크
    public ResponseEntity<?> checkUserId(String email) {
        try {
            if (userRepository.findByEmail(email).isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("msg", "사용가능한 아이디 입니다.");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String > response = new HashMap<>();
                response.put("msg", "이미 사용 중인 아이디 입니다.");
                response.put("http_status", HttpStatus.FORBIDDEN.toString());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("msg", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }
    }
    //닉네임 중복 체크
    public ResponseEntity<?> checkUserNickname(String nickname) {
        try {
            if (userRepository.findByNickname(nickname) == null) {
                Map<String, String> response = new HashMap<>();
                response.put("msg", "사용가능한 닉네임 입니다.");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("msg", "이미 사용 중인 닉네임 입니다.");
                response.put("http_status", HttpStatus.FORBIDDEN.toString());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("msg", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }
    }

    public ResponseEntity<?> findUserId(String  name, String  mobile) {
        List<User> user = userRepository.findEmailByNameAndMobile(name, mobile);

        List<String> email = user.stream().map(User::getEmail).toList();

        try {
            if (!user.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("ID", email.toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("msg", "회원 정보를 찾을 수없습니다.");
                response.put("http_status", HttpStatus.NOT_FOUND.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("msg", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }
    }

    public ResponseEntity<?> setNewPassword(FindDto findDto){
        User user = userRepository.findByNameAndMobileAndEmail(findDto.getName(),findDto.getMobile(),findDto.getEmail());

        if(user!=null) {
            user.setPassword(passwordEncoder.encode(findDto.getNewPassword()));
            userRepository.save(user);
            Map<String, String> response = new HashMap<>();
            response.put("msg", "비밀번호 변경이 완료 되었습니다");
            response.put("http_status", HttpStatus.OK.toString());
            return ResponseEntity.ok(response);
        }else {
            Map<String, String> response = new HashMap<>();
            response.put("msg", "해당하는 회원을 찾을 수 없습니다");
            response.put("http_status", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    //소셜 로그인
    @Transactional
    public ResponseEntity<?> socialLogin(User user,HttpServletRequest request ,HttpServletResponse httpServletResponse) {

        String email = user.getEmail();
        String password = user.getRawPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email,password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 회원이 없을 경우 예외 처리
            User isUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 없습니다"));

            // 로그아웃 토큰이 있는 경우 삭제
            if (redisTemplate.opsForValue().get("logout: " + email) != null) {
                redisTemplate.delete("logout: " + email);
            }

            String accessToken = tokenProvider.createAccessToken(email);
            String refreshToken = tokenProvider.createRefreshToken(email);

            redisTemplate.opsForValue().set(email, accessToken, Duration.ofSeconds(1800));
            redisTemplate.opsForValue().set("RF: " + email, refreshToken, Duration.ofHours(1L));


            Cookie accessCookie = new Cookie("access_token", accessToken);
            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);

            accessCookie.setPath("/oauth/login");


            httpServletResponse.addCookie(accessCookie);
            httpServletResponse.addCookie(refreshCookie);

            Map<String, String> response = new HashMap<>();
            response.put("message", "로그인 되었습니다");
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("http_status", HttpStatus.OK.toString());
            response.put("nickname", isUser.getNickname());
            response.put("id", isUser.getUserId().toString());

            httpServletResponse.addHeader("access_token", accessToken);
            httpServletResponse.addHeader("refresh_token", refreshToken);
            log.info(accessToken);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "잘못된 자격 증명입니다");
            response.put("http_status", HttpStatus.UNAUTHORIZED.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);


        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "가입되지 않은 회원입니다");
            response.put("http_status", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "알 수 없는 오류가 발생했습니다");
            response.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }
    //이메일 비밀번호 삭제
    public ResponseEntity<?> deleteUser(String token, LoginDto loginDto){
        User user = checkUserByToken(token);

        if(user.getEmail().equals(loginDto.getEmail())){
            if(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
                userRepository.delete(user);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("유저 비밀번호가 다릅니다");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("유저 아이디가 다릅니다");
        }

        return ResponseEntity.ok("회원이 삭제되었습니다");
    }

    public ResponseEntity<?> refresh(String token){
        User user = checkUserByToken(token);
        String email = user.getEmail();
        if(redisTemplate.opsForValue().get("RF: " + email) !=null){
            String accessToken = tokenProvider.createAccessToken(email);
            redisTemplate.opsForValue().set(email, accessToken, Duration.ofSeconds(1800));
            Map<String, String> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("http_status", HttpStatus.CREATED.toString());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다");
    }


}
