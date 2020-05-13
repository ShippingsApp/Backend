package com.shippings.controllers;

import com.shippings.model.*;
import com.shippings.payload.request.*;
import com.shippings.payload.response.*;
import com.shippings.repositories.*;
import com.shippings.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comm")
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
    public ResponseEntity<?> addComment(@RequestBody final AddCommentRequest AddRequest) {

        Comment comment = new Comment();
        comment.setIdFrom(getCurrentUserId());
        comment.setIdAbout(shipRepository.getOne(Long.parseLong(AddRequest.getId())).getDriverId());
        comment.setComment(AddRequest.getComment());
        comment.setRate(Integer.parseInt(AddRequest.getRate()));
        comment.setDate(new Date((new java.util.Date()).getTime()));

        commentRepository.save(comment);

        return ResponseEntity.ok(new MessageResponse("Комментарий успешно добавлен!"));
    }

    @GetMapping("/getComments")
    public List<Map<String, String>> getComments(final String driverName) {
        List<Comment> comments = commentRepository.findAllByIdAboutOrderByDateDesc(userRepository.getOneByUsername(driverName).getId());
         List<Map<String, String>> commList = new ArrayList();

        for (Comment c : comments){
            commList.add(new HashMap<String, String>(){{
                put("nameFrom", userRepository.getOne(c.getIdFrom()).getUsername());
                put("rate", rateToString(c.getRate()));
                put("comment", c.getComment());
                put("date", c.getDate().toString());
            }});}
    return commList;
    }

    public static final int PERFECT=5;
    public static final int GOOD=4;
    public static final int NORMAL=3;
    public static final int BAD=2;
    public static final int HORRIBLE=1;

    String rateToString(final int rt){
        String rate="";
        switch (rt){
            case(PERFECT):
                rate="Превосходно";
                break;
            case(GOOD):
                rate="Хорошо";
                break;
            case(NORMAL):
                rate="Нормально";
                break;
            case(BAD):
                rate="Плохо";
                break;
            case(HORRIBLE):
                rate="Ужасно";
                break;
        }
        return rate;
    }

    @GetMapping("/getRate")
    public float getRate(final String driverName){
        List<Comment> comments = commentRepository.findAllByIdAboutOrderByDateDesc(userRepository.getOneByUsername(driverName).getId());
        if(comments.size()==0) return 2.5f;
        float average=0;
        for (Comment c : comments){average+=c.getRate();}
        return average/comments.size();
    }
}
