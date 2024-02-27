package com.git.backend.daeng_nyang_connect.oauth.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.config.security.SecurityConfig;
import com.git.backend.daeng_nyang_connect.user.dto.AddExtraInfoDto;
import com.git.backend.daeng_nyang_connect.user.entity.MyPage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.MyPageRepository;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import com.git.backend.daeng_nyang_connect.user.role.Role;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import jakarta.persistence.Cacheable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Cacheable
public class OAuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final MyPageRepository myPageRepository;
    private final SecurityConfig securityConfig;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${naverIdEc2}")
    private String naver_client_id;

    @Value("${naverSecretEc2}")
    private String naver_client_secret;

    @Value("${kakaoIdEc2}")
    private String kakao_client_id;

    public ResponseEntity<?> naverLogin(HttpServletRequest request, HttpServletResponse response){
        // 네이버에서 전달해준 code, state 값 가져오기
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        String tokenURL = "https://nid.naver.com/oauth2.0/token";

        // body data 생성
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("grant_type", "authorization_code");
        parameter.add("client_id", naver_client_id);
        parameter.add("client_secret", naver_client_secret);
        parameter.add("code", code);
        parameter.add("state", state);

        // request header 설정
        HttpHeaders headers = new HttpHeaders();
        // Content-type을 application/x-www-form-urlencoded 로 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // header 와 body로 Request 생성
        HttpEntity<?> entity = new HttpEntity<>(parameter, headers);
        try {
            RestTemplate restTemplate = new RestTemplate();
            // 응답 데이터(json)를 Map 으로 받을 수 있도록 관련 메시지 컨버터 추가
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            // Post 방식으로 Http 요청
            // 응답 데이터 형식은 Hashmap 으로 지정
            ResponseEntity<HashMap> result = restTemplate.postForEntity(tokenURL, entity, HashMap.class);
            Map<String, String> resMap = result.getBody();
            log.info("resMap" + resMap);

            // 리턴받은 access_token 가져오기
            String access_token = resMap.get("access_token");

            String userInfoURL = "https://openapi.naver.com/v1/nid/me";
            // Header에 access_token 삽입
            headers.set("Authorization", "Bearer " + access_token);

            // Request entity 생성
            HttpEntity<?> userInfoEntity = new HttpEntity<>(headers);

            // Post 방식으로 Http 요청
            // 응답 데이터 형식은 Hashmap 으로 지정
            ResponseEntity<HashMap> userResult = restTemplate.postForEntity(userInfoURL, userInfoEntity, HashMap.class);

            HashMap<String, Object> responseMap = (HashMap<String, Object>) userResult.getBody().get("response");
            String email = (String) responseMap.get("email");
            String name = (String) responseMap.get("name");
            String nickname = (String) responseMap.get("nickname");
            String mobile = (String) responseMap.get("mobile");

            Optional<User> byEmail = userRepository.findByEmail(email);

            if (byEmail.isEmpty()) {
                User naverUser = new User();
                naverUser.setEmail(email);
                naverUser.setRole(Role.USER);
                naverUser.setName(name);
                naverUser.setNickname(nickname);
                naverUser.setMobile(mobile);
                naverUser.setRawPassword("naver123!");
                naverUser.setPassword(securityConfig.passwordEncoder().encode("naver123!"));
                userRepository.save(naverUser);
                MyPage myPage = userService.myPageEntity(naverUser);
                myPageRepository.save(myPage);
                userService.socialLogin(naverUser,request,response);
                response.sendRedirect("https://daeng-nyang-connect-fe.vercel.app/oauthAddInfo");
                return ResponseEntity.ok(response);
            } else {
                User user = byEmail.get();
                userService.socialLogin(user,request,response);
                response.sendRedirect("https://daeng-nyang-connect-fe.vercel.app/socialLogin");
                return ResponseEntity.ok(response);
            }
        }catch (RestClientException ex) {
            ex.printStackTrace();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> rs = new HashMap<>();
        rs.put("message", "알 수 없는 오류가 발생했습니다");
        rs.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);

    }

    public ResponseEntity<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response){
        // 카카오에서 전달해준 code, state 값 가져오기
        String code = request.getParameter("code");

        String tokenURL = "https://kauth.kakao.com/oauth/token";
        String redirect_uri = "https://daeng-nyang-be-qyu5xzcspa-du.a.run.app/oauth/kakao";


        // body data 생성
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("grant_type", "authorization_code");
        parameter.add("client_id", kakao_client_id);
        parameter.add("code", code);
        parameter.add("redirect_uri", redirect_uri);


        // request header 설정
        HttpHeaders headers = new HttpHeaders();
        // Content-type을 application/x-www-form-urlencoded 로 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // header 와 body로 Request 생성
        HttpEntity<?> entity = new HttpEntity<>(parameter, headers);
        try {
            RestTemplate restTemplate = new RestTemplate();
            // 응답 데이터(json)를 Map 으로 받을 수 있도록 관련 메시지 컨버터 추가
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            // Post 방식으로 Http 요청
            // 응답 데이터 형식은 Hashmap 으로 지정
            ResponseEntity<HashMap> result = restTemplate.postForEntity(tokenURL, entity, HashMap.class);
            Map<String, String> resMap = result.getBody();
            log.info("resMap" + resMap);

            // 리턴받은 access_token 가져오기
            String access_token = resMap.get("access_token");

            String userInfoURL = "https://kapi.kakao.com/v2/user/me";
            // Header에 access_token 삽입
            headers.set("Authorization", "Bearer " + access_token);

            // Request entity 생성
            HttpEntity<?> userInfoEntity = new HttpEntity<>(headers);

            // Post 방식으로 Http 요청
            // 응답 데이터 형식은 Hashmap 으로 지정
            ResponseEntity<HashMap> userResult = restTemplate.postForEntity(userInfoURL, userInfoEntity, HashMap.class);

            HashMap<String, Object> responseMap = (HashMap<String, Object>) userResult.getBody().get("kakao_account");
            HashMap<String, Object> rs = (HashMap<String, Object>) responseMap.get("profile");
            log.info("response map : " + rs.toString());
            String name = (String) rs.get("nickname");
            String nickName = (String) rs.get("nickname");
            String profileImg = (String) rs.get("thumbnail_image_url");
            log.info("name" + nickName);

            User isUser = userRepository.findByName(name);

            if(isUser==null){
                User kakao = new User();
                kakao.setEmail(UUID.randomUUID().toString().substring(0, 8)+"@kakao.com");
                kakao.setName(name);
                kakao.setNickname("kakao :" +nickName);
                kakao.setRole(Role.USER);
                kakao.setRawPassword("kakao123!");
                kakao.setPassword(securityConfig.passwordEncoder().encode("kakao123!"));
                userRepository.save(kakao);
                MyPage myPage = userService.myPageEntity(kakao);
                myPage.setImg(profileImg);
                myPageRepository.save(myPage);
                userService.socialLogin(kakao,request,response);
                response.sendRedirect("https://daeng-nyang-connect-fe.vercel.app/oauthAddInfo");
                return ResponseEntity.ok(response);

            }else{
                userService.socialLogin(isUser,request ,response);
                response.sendRedirect("https://daeng-nyang-connect-fe.vercel.app/socialLogin");
                return ResponseEntity.ok(response);
            }
            }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch (RestClientException rex){
            rex.printStackTrace();
        }
        Map<String, String> rs = new HashMap<>();
        rs.put("message", "알 수 없는 오류가 발생했습니다");
        rs.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
    }

    //네이버 로그인 시 추가 정보 입력 API
    public ResponseEntity<?>naverAddInfo(String token, AddExtraInfoDto addExtraInfoDto){
        User user = userService.checkUserByToken(token);
        try{
            user.setCity(addExtraInfoDto.getCity());
            user.setTown(addExtraInfoDto.getTown());
            user.setExperience(addExtraInfoDto.getExperience());
            user.setGender(addExtraInfoDto.getGender());
            user.setNickname(addExtraInfoDto.getNickname());
            userRepository.save(user);
        }catch (NotFoundException e){
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("msg", "잘못된 요청 입니다.");
            response.put("http_status", HttpStatus.NOT_ACCEPTABLE.toString());
        }
        Map<String, String> response = new HashMap<>();
        response.put("msg", "개인정보 추가가 성공적으로 되었습니다");
        response.put("http_status", HttpStatus.CREATED.toString());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?>kakaoAddInfo(String token, AddExtraInfoDto addExtraInfoDto){
        User user = userService.checkUserByToken(token);
        try{
            user.setCity(addExtraInfoDto.getCity());
            user.setTown(addExtraInfoDto.getTown());
            user.setExperience(addExtraInfoDto.getExperience());
            user.setGender(addExtraInfoDto.getGender());
            user.setNickname(addExtraInfoDto.getNickname());
            userRepository.save(user);
        }catch (NotFoundException e){
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("msg", "잘못된 요청 입니다.");
            response.put("http_status", HttpStatus.NOT_ACCEPTABLE.toString());
        }
        Map<String, String> response = new HashMap<>();
        response.put("msg", "개인정보 추가가 성공적으로 되었습니다");
        response.put("http_status", HttpStatus.CREATED.toString());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> oauthLogin(String token, HttpServletRequest request,HttpServletResponse response){
        String email = tokenProvider.getEmailBytoken(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        return userService.socialLogin(user, request, response);
    }







}

