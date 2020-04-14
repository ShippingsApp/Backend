package com.shippings.controllers;

import com.shippings.security.services.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "*Информация о том, для чего нужен сайт*";
    }

    @GetMapping("/client")
    @PreAuthorize("hasAuthority('client')")
    public String clientAccess() {
        return "*Предложения от водителей*";
    }

    @GetMapping("/driver")
    @PreAuthorize("hasAuthority('driver')")
    public String driverAccess() {
        return "*Мои поездки*";
    }
}