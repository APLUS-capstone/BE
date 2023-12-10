package sg.hsdd.aplus.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import sg.hsdd.aplus.entity.UserInfo;
import sg.hsdd.aplus.repository.UserInfoRepository;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuth2Service {


    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserInfoRepository userInfoRepository;
    private final JwtTokenProvider jwtTokenProvider;
//    private final UserTokenRepository userTokenRepository;


    @Transactional
    public LoginResponse login(String providerName, String code){
        ClientRegistration provider = clientRegistrationRepository.findByRegistrationId("kakao");

        log.info("{}===>code", code);
        log.info("{}===>getTokenUri", provider.getProviderDetails().getTokenUri());

        OAuth2TokenResponse tokenResponse = getToken(code, provider);

//        User user = getUserProfile(providerName, tokenResponse.getAccessToken(), provider);
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse.getAccessToken());
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(userAttributes);

        log.info("{}=====>1", kakaoUserInfo.getName());
        log.info("{}=====>2", kakaoUserInfo.getEmail());
        log.info("{}=====>3", kakaoUserInfo.getProviderId());
        log.info("{}=====>4", kakaoUserInfo.getKakaoAccount().get("profile"));
        log.info("{}=====>5", kakaoUserInfo.getAttributes());
        log.info("{}=====>6", kakaoUserInfo.getKakaoAccount());
        log.info("{}=====>8", kakaoUserInfo);




//        String name = kakaoUserInfo.getName();
        String name = kakaoUserInfo.getKakaoAccount().get("profile").toString().substring(10);
        name = name.substring(0, name.length()-1);
        log.info("{}=====>name to save", name);
        String email = kakaoUserInfo.getEmail();
//        String gender = kakaoUserInfo.getGender();
//        Long age = Long.parseLong(kakaoUserInfo.getAge().substring(0,1));
//        age *= 10;



        Optional<UserInfo> user = userInfoRepository.findByEmail(email);

        UserInfo createUser = null;
        Boolean isNew = false;
        if(user.isEmpty()){
            createUser = userInfoRepository.save(
                    UserInfo.builder()
                            .name(name)
                            .email(email)
                            .build())
            ;
            isNew = true;
        } else {
            createUser = user.get();
//            if(user.get().getStatus().equals(UserStatus.CREATED)){
//                isNew = true;
//            }
        }

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(createUser.getUserUid()));
        String refreshToken = jwtTokenProvider.createRefreshToken();

//        UserToken userToken = null;
//        Optional<UserToken> optionalUserToken = userTokenRepository.findByUserId(createUser.getId());
//        if(optionalUserToken.isPresent()) {
//            userToken = optionalUserToken.get();
//            userToken.setRefreshToken(refreshToken);
//        }else {
//            userToken = UserToken.builder().userId(createUser.getId()).refreshToken(refreshToken).build();
//        }
//        userTokenRepository.save(userToken);

        return LoginResponse.builder()
                .id(createUser.getUserUid())
                .name(createUser.getName())
                .email(createUser.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNew(isNew)
//                .age(null)
                .build();

    }

    private OAuth2TokenResponse getToken(String code, ClientRegistration provider){

        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(header-> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OAuth2TokenResponse.class)
                .block();


    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", provider.getClientId());
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("code", code);
        formData.add("client_secret", provider.getClientSecret());
        return formData;
    }

    private Map<String, Object> getUserAttributes(ClientRegistration provider, String oAuth2AccessToken){
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header->header.setBearerAuth(oAuth2AccessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

}
