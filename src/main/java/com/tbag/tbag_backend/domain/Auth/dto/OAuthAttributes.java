package com.tbag.tbag_backend.domain.Auth.dto;

import com.tbag.tbag_backend.domain.Auth.Enum.SocialType;
import com.tbag.tbag_backend.domain.User.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String birth;
    private final String gender;
    private final String age_range;
    private final String profile_image;
    private final String oauthId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String birth, String gender, String age_range, String profile_image, String oauthId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.age_range = age_range;
        this.profile_image = profile_image;
        this.oauthId = oauthId;
    }

    public static OAuthAttributes of(String socialName, String userNameAttributeName, Map<String, Object> attributes) {

        if ("kakao".equals(socialName)) {
            return ofKakao(userNameAttributeName, attributes);
        }

        if ("google".equals(socialName)) {
            return ofGoogle(userNameAttributeName, attributes);
        }

        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        String id = attributes.get("id").toString();

        return OAuthAttributes.builder()
                .name(generateRandomNickname())
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .oauthId(id)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {

        String id = attributes.get("sub").toString();

        return OAuthAttributes.builder()
                .name(generateRandomNickname())
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .oauthId(id)
                .build();
    }


    public User toEntity(String registrationId) {
        return User.builder()
                .nickname(name)
                .socialId(oauthId)
                .socialType(SocialType.valueOf(registrationId.toUpperCase(Locale.ROOT)))
                .build();
    }

    private static String generateRandomNickname() {
        return "User_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}