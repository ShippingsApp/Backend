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

import javax.validation.constraints.Null;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rqst")
@Slf4j
public class RequestController {


    @Autowired
    RequestRepository RequestRepository;

    @Autowired
    ShippRepository ShipRepository;

    @Autowired
    UserRepository UserRepository;

    @GetMapping("/client")
    @PreAuthorize("hasAuthority('client')")
    public String clientAccess() {
        return "Client Content.";
    }

    long getCurrentUserId() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();;
        return user.getId();
    }

    @GetMapping("/clientRequest")
    @PreAuthorize("hasAuthority('client')")
    public List<Map<String, String>> clientRequest(Integer status) {

    List<Map<String, String>> rqstList = new ArrayList();
        log.info(String.format("started"));
        List<Request> rqst = RequestRepository.findAllByUserFromIdAndStatus(this.getCurrentUserId(), status);
        log.info(String.format("rqst.size is %d", rqst.size()));
        for (Request r : rqst){

            rqstList.add(new HashMap<String, String>(){{
                put("id", Long.toString(r.getId()));
                put("driverName", UserRepository.getOne(ShipRepository.getOne(r.getShippingId()).getDriverId()).getUsername());
                put("driverPhone", UserRepository.getOne(ShipRepository.getOne(r.getShippingId()).getDriverId()).getMobilePhone());
                put("start", r.getStart());
                put("finish", r.getFinish());
                put("weight", Integer.toString(r.getWeight()));
                put("height", Integer.toString(r.getHeight()));
                put("length", Integer.toString(r.getLength()));
                put("width", Integer.toString(r.getWidth()));
                put("comment", r.getComment());
                put("price", Integer.toString(r.getPrice()));
            }});}

        return rqstList;
    }

    @PostMapping("/requestup")
    public ResponseEntity<?> addRequest(@RequestBody AddClientRequest AddRequest) {
        log.info(String.format("request adding started"));

        Request rqst = new Request();
        rqst.setStart(AddRequest.getStart());
        rqst.setFinish(AddRequest.getFinish());
        rqst.setWeight(Integer.parseInt(AddRequest.getWeight()));
        rqst.setHeight(Integer.parseInt(AddRequest.getHeight()));
        rqst.setLength(Integer.parseInt(AddRequest.getLength()));
        rqst.setWidth(Integer.parseInt(AddRequest.getWidth()));
        rqst.setPrice(Integer.parseInt(AddRequest.getPrice()));
        rqst.setComment(AddRequest.getComment());
        rqst.setStatus(0);
        rqst.setUserFromId(this.getCurrentUserId());
        rqst.setShippingId(Long.parseLong(AddRequest.getId()));
        log.info(String.format("request added"));
        RequestRepository.save(rqst);
        Shipping ship=ShipRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(ship.getStatus()){
            ship.setStatus(Boolean.FALSE);
            ShipRepository.save(ship);
        };

        return ResponseEntity.ok(new MessageResponse("Ваша заявка отправлена водителю!"));
    }

    @GetMapping("/getRequest")
    //@PreAuthorize("hasAuthority('client')")
    public Request getRequest(long ID){
        log.info("request request is received");
        Request rqst = RequestRepository.findOneById(ID);
        return rqst;
    }

    @PostMapping("/rqstedit")
    public ResponseEntity<?> editRequest(@RequestBody AddClientRequest AddRequest) {
        log.info(String.format("request editing started"));

        Request rqst = RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}

        if(AddRequest.getStart()!= null){rqst.setStart(AddRequest.getStart());}
        if(AddRequest.getFinish()!= null){rqst.setFinish(AddRequest.getFinish());}
        if(AddRequest.getWeight()!= null){rqst.setWeight(Integer.parseInt(AddRequest.getWeight()));}
        if(AddRequest.getHeight()!= null){rqst.setHeight(Integer.parseInt(AddRequest.getHeight()));}
        if(AddRequest.getLength()!= null){rqst.setLength(Integer.parseInt(AddRequest.getLength()));}
        if(AddRequest.getWidth()!= null){rqst.setWidth(Integer.parseInt(AddRequest.getWidth()));}
        if(AddRequest.getPrice()!= null){rqst.setPrice(Integer.parseInt(AddRequest.getPrice()));}
        if(AddRequest.getComment()!= null){rqst.setComment(AddRequest.getComment());}
        log.info(String.format("request edited"));
        RequestRepository.save(rqst);

        return ResponseEntity.ok(new MessageResponse("Маршрут исправлен успешно!"));
    }

    @PostMapping("/deleteRqst")
    public ResponseEntity<?> deleteRequest(@RequestBody AddClientRequest AddRequest) {
        log.info(String.format("delete started"));
        Request rqst=RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}
        RequestRepository.delete(rqst);

        return ResponseEntity.ok(new MessageResponse(" Успешно удалено "));
    }

}

