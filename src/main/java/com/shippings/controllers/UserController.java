package com.shippings.controllers;

import com.shippings.model.User;
import com.shippings.payload.request.EditInfoRequest;
import com.shippings.payload.response.MessageResponse;
import com.shippings.security.services.UserDetailsImpl;
import com.shippings.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PatchMapping("/editprofile/{username}")
    public ResponseEntity<?> editProfile(Authentication authentication,
                                         @PathVariable String username, @RequestBody EditInfoRequest patch) {

        if (((UserDetailsImpl) authentication.getPrincipal()).getUsername().equals(username)) {

            User user = service.getByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь с данным никнеймом не найден: " + username));
            Optional.of(patch.getRealName()).ifPresent(patchUsername -> user.setRealName(patchUsername));
            Optional.of(patch.getMobilePhone()).ifPresent(patchMobilePhone -> user.setRealName(patchMobilePhone));

            service.save(user);
            return ResponseEntity.ok(new MessageResponse("Изменения сохранены"));
        }
        else
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("В доступе отказано"));
    }
}
