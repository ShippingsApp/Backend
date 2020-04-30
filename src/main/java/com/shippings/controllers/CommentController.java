package com.shippings.controllers;

import com.shippings.model.*;
import com.shippings.payload.request.*;
import com.shippings.payload.response.*;
import com.shippings.repositories.*;
import com.shippings.security.services.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comm")
@Slf4j
public class CommentController {

    @Autowired
    ShippRepository shipRepository;

    @Autowired
    CommentRepository commentRepository;

    long getCurrentUserId() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();;
        return user.getId();
    }

    @PostMapping("/commentup")
    public ResponseEntity<?> addComment(@RequestBody AddCommentRequest AddRequest) {
        log.info(String.format("comment adding started"));

        Comment comm = new Comment();
        comm.setIdFrom(getCurrentUserId());
        comm.setIdAbout(shipRepository.getOne(Long.parseLong(AddRequest.getId())).getDriverId());
        comm.setComment(AddRequest.getComment());
        comm.setRate(Integer.parseInt(AddRequest.getRate()));
        comm.setDate(new Date((new java.util.Date()).getTime()));

        commentRepository.save(comm);

        return ResponseEntity.ok(new MessageResponse("Comment added successfully!"));
    }

}
