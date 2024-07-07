package com.tbag.tbag_backend.domain.User.controller;


import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto.UserRegistrationRequest;
import com.tbag.tbag_backend.domain.Content.ContentSearchDto;
import com.tbag.tbag_backend.domain.Content.ContentService;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.UpdatePreferredGenresRequest;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import com.tbag.tbag_backend.domain.Location.service.ContentLocationService;
import com.tbag.tbag_backend.domain.User.dto.UpdateNicknameRequest;
import com.tbag.tbag_backend.domain.User.dto.UserDto;
import com.tbag.tbag_backend.domain.User.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ContentService contentService;
    private final ContentLocationService contentLocationService;

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

    @PostMapping("/{userId}/update-nickname")
    public ResponseEntity<Void> updateNickname(@PathVariable Integer userId, @RequestBody UpdateNicknameRequest request, Principal principal) {
        userService.updateNickname(userId, request.getNickname(), principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/update-preferred-genres")
    public ResponseEntity<Void> updatePreferredGenres(@PathVariable Integer userId, @RequestBody UpdatePreferredGenresRequest request, Principal principal) {
        userService.updatePreferredGenres(userId, request.getPreferredGenres(), principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/contents-history")
    public Page<ContentSearchDto> getHistoryContents(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentService.getHistoryContents(PageRequest.of(page, size), userId);
    }

    @GetMapping("/{userId}/locations-history")
    public Page<ContentLocationDetailDto> getHistoryContentLocationss(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentLocationService.getHistoryContentLocationss(PageRequest.of(page, size), userId);
    }

}
