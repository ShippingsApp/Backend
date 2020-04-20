package com.shippings.controllers;

import com.shippings.model.Shipping;
import com.shippings.payload.request.AddRoutRequest;
import com.shippings.payload.response.MessageResponse;
import com.shippings.repositories.ShippRepository;
import com.shippings.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ship")
public class DriverController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    ShippRepository shipRepository;
    
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
        List<Shipping> shipps = shipRepository.findAllByDriverIdAndStatusOrderByDateStartDesc(this.getCurrentUserId(), bool);
        LOG.info(String.format("shipp.size is %d", shipps.size()));
        for (Shipping s : shipps)
            shippList.add(new HashMap<String, String>(){{
                put("id", Long.toString(s.getId()));
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

    @GetMapping("/getRoute")
    @PreAuthorize("hasAuthority('driver')")
    public Shipping getRoute(long ID){
        LOG.info("router request is received");
        Shipping shipp = shipRepository.findOneById(ID);
        return shipp;
    }

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
        ship.setDriverId(this.getCurrentUserId());
        //ship.setDriverId((long)1);
        LOG.info(String.format("shipp added"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Ship added successfully!"));
    }


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
        LOG.info(String.format("shipp edited"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Ship edited successfully!"));
    }

    @PostMapping("/refuseShip")
    public ResponseEntity<?> refuseShip(@RequestBody AddRoutRequest AddRequest) {
        LOG.info(String.format("refuse started"));
        Shipping ship = shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        ship.setStatus(Boolean.TRUE);
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse(" refuse suss "));
    }
}
