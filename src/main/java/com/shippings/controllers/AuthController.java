package com.shippings.controllers;

import com.shippings.model.Shipping;
import com.shippings.model.User;
import com.shippings.payload.request.*;
import com.shippings.payload.response.JwtResponse;
import com.shippings.payload.response.MessageResponse;
import com.shippings.repositories.ShippRepository;
import com.shippings.repositories.UserRepository;
import com.shippings.security.jwt.JwtUtils;
import com.shippings.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                userDetails.getRealName(),
                userDetails.getMobilePhone(),
                userDetails.getRate(),
                userDetails.getStatus()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setRole(signUpRequest.getChosenRole());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRealName(signUpRequest.getRealName());
        user.setMobilePhone(signUpRequest.getMobilePhone());
        user.setStatus(0);
        user.setRate(0);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



    // Все нижеследующее должно быть венесено в отдельный блок !!!
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ShippRepository shipRepository;
    @PostMapping("/routeup")
    public ResponseEntity<?> addRouter(@RequestBody AddRoutRequest AddRequest) {
        LOG.info(String.format("shipp adding started"));

        Shipping ship = new Shipping();
        ship.setDateStart(Date.valueOf(AddRequest.getDateStart()));
        ship.setDateFinish(Date.valueOf(AddRequest.getDateFinish()));
        ship.setStart(AddRequest.getStart());
        ship.setFinish(AddRequest.getFinish());
        ship.setWeight(Integer.parseInt(AddRequest.getWeight()));
        ship.setHeight(Integer.parseInt(AddRequest.getHeight()));
        ship.setLength(Integer.parseInt(AddRequest.getLength()));
        ship.setWidth(Integer.parseInt(AddRequest.getWidth()));
        ship.setPlusTime(Integer.parseInt(AddRequest.getPlusTime()));
        ship.setComment(AddRequest.getComment());
        ship.setStatus(Boolean.TRUE);
        //ship.setDriverId(this.getCurrentUserId());
        ship.setDriverId((long)1);
        LOG.info(String.format("shipp added"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Ship added successfully!"));
    }

 /*   long getCurrentUserId() {
        UserDetailsImpl user = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }*/

    @PostMapping("/routedit")
    public ResponseEntity<?> editRouter(@RequestBody AddRoutRequest AddRequest) {
        LOG.info(String.format("shipp editing started"));

        Shipping ship = shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        LOG.info(String.format("ship id"+ship.getId()));
        if(!AddRequest.getDateStart().trim().isEmpty()){ship.setDateStart(Date.valueOf(AddRequest.getDateStart()));}
        if(!AddRequest.getDateFinish().trim().isEmpty()){ship.setDateFinish(Date.valueOf(AddRequest.getDateFinish()));}
        if(!AddRequest.getStart().trim().isEmpty()){ship.setStart(AddRequest.getStart());}
        if(!AddRequest.getFinish().trim().isEmpty()){ship.setFinish(AddRequest.getFinish());}
        if(!AddRequest.getWeight().trim().isEmpty()){ship.setWeight(Integer.parseInt(AddRequest.getWeight()));}
        if(!AddRequest.getHeight().trim().isEmpty()){ship.setHeight(Integer.parseInt(AddRequest.getHeight()));}
        if(!AddRequest.getLength().trim().isEmpty()){ship.setLength(Integer.parseInt(AddRequest.getLength()));}
        if(!AddRequest.getWidth().trim().isEmpty()){ship.setWidth(Integer.parseInt(AddRequest.getWidth()));}
        if(!AddRequest.getPlusTime().trim().isEmpty()){ship.setPlusTime(Integer.parseInt(AddRequest.getPlusTime()));}
        if(!AddRequest.getComment().trim().isEmpty()){ship.setComment(AddRequest.getComment());}
        //ship.setDriverId(this.getCurrentUserId());
        LOG.info(String.format("shipp edited"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Ship edited successfully!"));
    }

}

