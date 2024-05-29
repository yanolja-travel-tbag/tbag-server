package com.tbag.tbag_backend.domain.Auth.dto.kakao;

import com.tbag.tbag_backend.domain.Auth.Enum.SocialType;
import com.tbag.tbag_backend.domain.User.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class KakaoOAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String birth;
    private final String gender;
    private final String age_range;
    private final String profile_image;
    private final Long oauthId;

    @Builder
    public KakaoOAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String birth, String gender, String age_range, String profile_image, Long oauthId) {
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

    public static KakaoOAuthAttributes of(String socialName, String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오
        if ("kakao".equals(socialName)) {
            return ofKakao(userNameAttributeName, attributes);
        }

        return null;
    }

    private static KakaoOAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Long id = (Long) attributes.get("id");

        return KakaoOAuthAttributes.builder()
                .name(generateRandomNickname())
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .oauthId(id)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .nickname(name)
                .socialId(oauthId)
                .socialType(SocialType.KAKAO)
                .build();
    }

    private static String generateRandomNickname() {
        return "User_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}