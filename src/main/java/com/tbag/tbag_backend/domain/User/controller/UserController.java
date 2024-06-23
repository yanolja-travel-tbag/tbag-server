package com.tbag.tbag_backend.domain.User.controller;


import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto.UserRegistrationRequest;
import com.tbag.tbag_backend.domain.User.dto.UserDto;
import com.tbag.tbag_backend.domain.User.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/{userId}/update-registration")
    public ResponseEntity<Void> updateUserRegistrationStatus(@PathVariable Integer userId, @RequestBody UserRegistrationRequest request, Principal principal) {
        userService.updateUserRegistrationStatus(userId, request.getPreferredGenres(), request.getPreferredArtists(), principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(Principal principal) {
        UserDto userDto = userService.getUserInfo(principal);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Integer userId, Principal principal) {
        userService.deactivateUser(userId, principal);
        return ResponseEntity.ok().build();
    }

}
