package com.shippings.controllers;

import com.shippings.security.services.UserDetailsImpl;
import com.shippings.model.User;
import com.shippings.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shippings.model.Shipping;
import com.shippings.repositories.ShippRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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

}
