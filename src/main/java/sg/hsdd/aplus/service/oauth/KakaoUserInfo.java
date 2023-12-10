package sg.hsdd.aplus.service.oauth;

import java.util.Map;

public class KakaoUserInfo {
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) { this.attributes = attributes; }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getProviderId() { return String.valueOf(attributes.get("id")); }

    public String getProvider() { return "kakao"; }

    public String getEmail() { return (String) getKakaoAccount().get("email");}

    public String getName() {
        return (String) getKakaoAccount().get("nickname");
    }

    public String getGender() {
        return (String) getKakaoAccount().get("gender");
    }

    public String getAge() {
        return (String) getKakaoAccount().get("age_range");
    }

    public Map<String, Object> getKakaoAccount(){
        return(Map<String, Object>) attributes.get("kakao_account");
    }

}
