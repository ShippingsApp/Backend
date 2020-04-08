package com.shippings.controllers;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/client")
    //@PreAuthorize("((UserDetailsImpl)principal).hasRole('client')")
    @PreAuthorize("hasAuthority('client')")
    public String clientAccess() {
        return "Client Content.";
    }

    @Autowired
    private ShippRepository shippRepository;

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    long getCurrentUserId() {
        UserDetailsImpl user = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();;
        return user.getId();
    }

    @GetMapping("/driver")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> driverAccess(){
        LOG.info("driver request is received");
        return getRouteList(Boolean.TRUE);
    }

    @GetMapping("/driverRequest")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> driverRequestAccess() {
        LOG.info("driver request request is received.");
       return getRouteList(Boolean.FALSE);
    }

    List<Map<String, String>> getRouteList(Boolean bool){
        List<Map<String, String>> shippList = new ArrayList();
        List<Shipping> shipps = shippRepository.findAllByDriverIdAndStatus(this.getCurrentUserId(), bool);
        LOG.info(String.format("shipp.size is %d", shipps.size()));
        for (Shipping s : shipps)
            shippList.add(new HashMap<String, String>(){{
                put("date_start", s.getDateStart().toString());
                put("date_finish", s.getDateFinish().toString());
                put("start", s.getStart());
                put("finish", s.getFinish());
                put("weight", Integer.toString(s.getWeight()));
                put("height", Integer.toString(s.getHeight()));
                put("length", Integer.toString(s.getLength()));
                put("width", Integer.toString(s.getWidth()));
                put("plus_time", Integer.toString(s.getPlusTime()));
                put("comment", s.getComment());
            }});

        return shippList;
    }

    @GetMapping("/addRoute")
    @PreAuthorize("hasAuthority('driver')")
    public String addRouteAccess(){
        LOG.info("addRoute");
        return ";3";
    }

}
