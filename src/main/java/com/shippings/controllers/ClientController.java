package com.shippings.controllers;

import com.shippings.model.Request;
import com.shippings.model.Shipping;
import com.shippings.model.User;
import com.shippings.payload.request.AddClientRequest;
import com.shippings.payload.request.AddRoutRequest;
import com.shippings.payload.response.MessageResponse;
import com.shippings.repositories.RequestRepository;
import com.shippings.repositories.ShippRepository;
import com.shippings.repositories.UserRepository;
import com.shippings.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rqst")
public class ClientController {
    @Autowired
    RequestRepository RequestRepository;

    @Autowired
    ShippRepository ShipRepository;

    @Autowired
    UserRepository UserRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

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
    public List<Map<String, String>> clientRequest(Boolean editable) {

    List<Map<String, String>> rqstList = new ArrayList();
        LOG.info(String.format("started"));
        List<Request> rqst = RequestRepository.findAllByUserFromIdAndStatus(this.getCurrentUserId(), editable);
        LOG.info(String.format("rqst.size is %d", rqst.size()));
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
        LOG.info(String.format("request adding started"));

        Request rqst = new Request();
        rqst.setStart(AddRequest.getStart());
        rqst.setFinish(AddRequest.getFinish());
        rqst.setWeight(Integer.parseInt(AddRequest.getWeight()));
        rqst.setHeight(Integer.parseInt(AddRequest.getHeight()));
        rqst.setLength(Integer.parseInt(AddRequest.getLength()));
        rqst.setWidth(Integer.parseInt(AddRequest.getWidth()));
        rqst.setPrice(Integer.parseInt(AddRequest.getPrice()));
        rqst.setComment(AddRequest.getComment());
        rqst.setStatus(Boolean.TRUE);
        rqst.setUserFromId(this.getCurrentUserId());
        rqst.setShippingId(Long.parseLong(AddRequest.getWeight()));
        LOG.info(String.format("request added"));
        RequestRepository.save(rqst);

        return ResponseEntity.ok(new MessageResponse("Request added successfully!"));
    }

    @GetMapping("/getRequest")
    @PreAuthorize("hasAuthority('client')")
    public Request getRequest(long ID){
        LOG.info("request request is received");
        Request rqst = RequestRepository.findOneById(ID);
        if(rqst.getUserFromId()!=getCurrentUserId()){return new Request();}
        return rqst;
    }

    @PostMapping("/rqstedit")
    public ResponseEntity<?> editRequest(@RequestBody AddClientRequest AddRequest) {
        LOG.info(String.format("request editing started"));

        Request rqst = RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}

        if(!AddRequest.getPrice().trim().isEmpty()){rqst.setPrice(Integer.parseInt(AddRequest.getPrice()));}
        if(!AddRequest.getComment().trim().isEmpty()){rqst.setComment(AddRequest.getComment());}
        LOG.info(String.format("request edited"));
        RequestRepository.save(rqst);

        return ResponseEntity.ok(new MessageResponse("Ship edited successfully!"));
    }

    @PostMapping("/deleteRqst")
    public ResponseEntity<?> deleteRequest(@RequestBody AddClientRequest AddRequest) {
        LOG.info(String.format("delete started"));
        Request rqst=RequestRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(rqst.getUserFromId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
        RequestRepository.delete(rqst);

        return ResponseEntity.ok(new MessageResponse(" delete suss "));
    }

}
