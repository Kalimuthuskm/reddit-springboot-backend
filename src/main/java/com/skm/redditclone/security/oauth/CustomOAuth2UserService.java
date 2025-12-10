package com.skm.redditclone.security.oauth;

import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfo.fromGoogle(oAuth2User.getAttributes());
        User user = processOAuth2User(registrationId,userInfo);

        return new CustomOAuth2User(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList(),
                oAuth2User.getAttributes()
        );
    }

    private User processOAuth2User(String provider,OAuth2UserInfo userInfo){
        Optional<User> userOptional = userRepository.findByOAuthProviderAndOAuthId(provider, userInfo.getId());
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
            user.setEmail(userInfo.getEmail());
            user.setProfileImageUrl(userInfo.getImageUrl());
            userRepository.updateOAuthUser(user);
        }
        else {
            user = new User();
            user.setUsername(userInfo.getName());
            user.setEmail(userInfo.getEmail());
            user.setOauthProvider(provider);
            user.setOauthId(userInfo.getId());
            user.setProfileImageUrl(userInfo.getImageUrl());
            user.setCreatedAt(Instant.now());
            user = userRepository.createOAuthUser(user);
        }
        return user;
    }
}
