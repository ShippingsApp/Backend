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
    UserRepository userRepository;

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

        return ResponseEntity.ok(new MessageResponse("Комментарий успешно добавлен!"));
    }

    @GetMapping("/getComments")
    public List<Map<String, String>> getComments(String driverName) {
        log.info(String.format("comments"));
        List<Comment> comm = commentRepository.findAllByIdAboutOrderByDateDesc(userRepository.getOneByUsername(driverName).getId());
        log.info(String.format("comm.size is %d", comm.size()));
        List<Map<String, String>> commList = new ArrayList();

        for (Comment c : comm){
            commList.add(new HashMap<String, String>(){{
                put("nameFrom", userRepository.getOne(c.getIdFrom()).getUsername());
                put("rate", rateToString(c.getRate()));
                put("comment", c.getComment());
                put("date", c.getDate().toString());
            }});}
    return commList;
    }

    String rateToString(int rt){
        String rate="";
        switch (rt){
            case(5):
                rate="Превосходно";
                break;
            case(4):
                rate="Хорошо";
                break;
            case(3):
                rate="Нормально";
                break;
            case(2):
                rate="Плохо";
                break;
            case(1):
                rate="Ужасно";
                break;
        }
        return rate;
    }

    @GetMapping("/getRate")
    public float getRate(String driverName){
        log.info(String.format("rate"));
        List<Comment> comm = commentRepository.findAllByIdAboutOrderByDateDesc(userRepository.getOneByUsername(driverName).getId());
        if(comm.size()==0) return 2.5f;
        log.info(String.format("comm.size is %d", comm.size()));
        float average=0;
        for (Comment c : comm){average+=c.getRate();}
        return average/comm.size();
    }

}
