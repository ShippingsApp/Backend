package com.shippings.controllers;

import com.shippings.model.*;
import com.shippings.payload.request.*;
import com.shippings.payload.response.*;
import com.shippings.repositories.*;
import com.shippings.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rqst")

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
    public List<Map<String, String>> clientRequest(final Integer status) {

    List<Map<String, String>> rqstList = new ArrayList();
        List<Request> rqst = RequestRepository.findAllByUserFromIdAndStatus(this.getCurrentUserId(), status);
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
    public ResponseEntity<?> addRequest(@RequestBody final AddClientRequest AddRequest) {

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
        rqst.setShippingId(Long.parseLong(AddRequest.getShipId()));
        RequestRepository.save(rqst);
        Shipping ship=ShipRepository.getOne(Long.parseLong(AddRequest.getShipId()));
        if(ship.getStatus()){
            ship.setStatus(Boolean.FALSE);
            ShipRepository.save(ship);
        };

        return ResponseEntity.ok(new MessageResponse("Ваша заявка отправлена водителю!"));
    }

    @GetMapping("/getRequest")
    public Request getRequest(final long ID){
        Request rqst = RequestRepository.findOneById(ID);
        return rqst;
    }

    @PostMapping("/rqstedit")
    public ResponseEntity<?> editRequest(@RequestBody final AddClientRequest AddRequest) {

        Request rqst = RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}

        Optional.ofNullable(AddRequest.getStart()).ifPresent(val -> rqst.setStart(val));
        Optional.ofNullable(AddRequest.getFinish()).ifPresent(val -> rqst.setFinish(val));
        Optional.ofNullable(AddRequest.getFinish()).ifPresent(val -> rqst.setFinish(val));
        Optional.ofNullable(AddRequest.getWeight()).ifPresent(val -> rqst.setWeight(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getHeight()).ifPresent(val -> rqst.setHeight(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getLength()).ifPresent(val -> rqst.setLength(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getWidth()).ifPresent(val -> rqst.setWidth(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getComment()).ifPresent(val -> rqst.setComment(val));
        RequestRepository.save(rqst);

        return ResponseEntity.ok(new MessageResponse("Маршрут исправлен успешно!"));
    }

    @PostMapping("/deleteRqst")
    public ResponseEntity<?> deleteRequest(@RequestBody final AddClientRequest AddRequest) {
        Request rqst=RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}
        RequestRepository.delete(rqst);

        return ResponseEntity.ok(new MessageResponse(" Успешно удалено "));
    }

}

