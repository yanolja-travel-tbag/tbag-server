package com.tbag.tbag_backend.domain.Auth.service;


import com.tbag.tbag_backend.domain.Auth.dto.OAuthAttributes;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(registrationId, attributes);

        Collection<SimpleGrantedAuthority> authorities;
        if (user.getIsRegistered()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"));
        }


        return new DefaultOAuth2User(
                authorities,
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(String registrationId, OAuthAttributes attributes) {
        Optional<User> user = userRepository.findBySocialIdAndIsActivatedIsTrue(attributes.getOauthId());
        if (!user.isPresent()) {
            return userRepository.save(attributes.toEntity(registrationId));
        }
        else if (user.get().getSocialId() == null){
            user.get().updateSocialId(attributes.getOauthId());
            userRepository.save(user.get());
        }
        return user.get();
    }
}
