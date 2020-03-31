package com.shippings.controllers;

import com.shippings.model.User;
import com.shippings.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<Map<String, String>> getAll() {
        LOG.info("getAll request is received.");

        List<Map<String, String>> userList = new ArrayList();
        List<User> users = service.getAll();
        LOG.info(String.format("users.size is %d", users.size()));
        for (User u : users)
            userList.add(new HashMap<String, String>(){{put("username", u.getUsername()); put("realname", u.getRealName());}});
        return userList;
    }

    @GetMapping("{id}")
    public User getOne(@PathVariable Long id) {
        return new User();
    }


}
