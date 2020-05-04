package com.shippings.controllers;

import com.shippings.model.*;
import com.shippings.payload.request.*;
import com.shippings.payload.response.*;
import com.shippings.repositories.*;
import com.shippings.security.services.UserDetailsImpl;
import com.shippings.services.ShippingService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ship")
@Slf4j
public class ShippingController {

    @Autowired
    ShippRepository shipRepository;

    @Autowired
    ShippingService shippingService;

    @Autowired
    RequestRepository rqstRepository;

    @Autowired
    UserRepository userRepository;


    long getCurrentUserId() {
        UserDetailsImpl user = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();;
        return user.getId();
    }

    public static final int PAST=1;
    public static final int PRESENT=2;
    public static final int FUTURE=3;

    @GetMapping("/driver")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> driverAccess(Integer time_per){
        log.info("driver request is received");
        return getRouteList(Boolean.TRUE, time_per);
    }

    @GetMapping("/driverRequest")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> driverRequestAccess(Integer status) {
        log.info("driver request request is received.");
       return getRouteShippedList(status);
    }

    List<Map<String, String>> getRouteShippedList(int status){

        List<Shipping> shipps = shipRepository.findAllByDriverIdAndStatusOrderByDateStartDesc(this.getCurrentUserId(), Boolean.FALSE);
        for (Iterator<Shipping> iter = shipps.listIterator(); iter.hasNext(); ) {
            Shipping a = iter.next();
            if (rqstRepository.findAllByShippingIdAndStatus(a.getId(),status).size()==0){
                iter.remove();
            }
        }

        return toListMap(shipps);
    }

    List<Map<String, String>> getRouteList(Boolean bool, int time_per){
        List<Map<String, String>> shippList = new ArrayList();
        List<Shipping> shipps = shipRepository.findAllByDriverIdAndStatusOrderByDateStartDesc(this.getCurrentUserId(), bool);

        switch(time_per){
            case(FUTURE):
                for (Iterator<Shipping> iter = shipps.listIterator(); iter.hasNext(); ) {
                    Shipping a = iter.next();
                    if ((new java.util.Date(a.getDateStart().getTime()).before(new java.util.Date()))){
                        iter.remove();
                    }
                }
                break;
            case(PRESENT):
                for (Iterator<Shipping> iter = shipps.listIterator(); iter.hasNext(); ) {
                    Shipping a = iter.next();
                    if (new java.util.Date(a.getDateFinish().getTime()).before(new java.util.Date())) {
                        iter.remove();
                    }
                    else{
                        if (new java.util.Date(a.getDateStart().getTime()).after(new java.util.Date())){
                            iter.remove();
                        }
                    }
                }
                break;
            case(PAST):
                for (Iterator<Shipping> iter = shipps.listIterator(); iter.hasNext(); ) {
                    Shipping a = iter.next();
                    if ((new java.util.Date(a.getDateFinish().getTime()).after(new java.util.Date()))) {
                        iter.remove();
                    }
                }
                break;
 }
    log.info(String.format("shipp.size is %d", shipps.size()));
        return toListMap(shipps);
        }

    List<Map<String, String>> toListMap(List<Shipping> shipps){
        List<Map<String, String>> shippList = new ArrayList();
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        for (Shipping s : shipps)
            shippList.add(new HashMap<String, String>(){{
                put("id", Long.toString(s.getId()));
                put("date_start", format1.format(s.getDateStart()));
                put("date_finish", format1.format(s.getDateFinish()));
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
    @PreAuthorize("hasAnyAuthority('driver', 'client')")
    public Shipping getRoute(long ID){
        log.info("router request is received");
        Shipping shipp = shipRepository.findOneById(ID);
        if(shipp.getDriverId()!=getCurrentUserId()){return new Shipping();}
        return shipp;
    }

    @GetMapping("/getShipRequests")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> getShipRequests(Long ID, Integer status){
        log.info("ship requests are received");
        if(shipRepository.getOne(ID).getDriverId()!=getCurrentUserId()){return null;}
        List<Map<String, String>> rqstList = new ArrayList();
        List<Request> rqst = rqstRepository.findAllByShippingIdAndStatus(ID,status);
        log.info(String.format("rqst.size is %d", rqst.size()));

        for (Request r : rqst)
            rqstList.add(new HashMap<String, String>(){{
                put("id", Long.toString(r.getId()));
                put("start", r.getStart());
                put("userFromName", userRepository.getOne(r.getUserFromId()).getUsername());
                put("userFromPhone", userRepository.getOne(r.getUserFromId()).getMobilePhone());
                put("finish", r.getFinish());
                put("weight", Integer.toString(r.getWeight()));
                put("height", Integer.toString(r.getHeight()));
                put("length", Integer.toString(r.getLength()));
                put("width", Integer.toString(r.getWidth()));
                put("comment", r.getComment());
                put("price", Integer.toString(r.getPrice()));
            }});

        return rqstList;
    }

    @PostMapping("/routeup")
    public ResponseEntity<?> addRouter(@RequestBody AddRoutRequest AddRequest) {
        log.info(String.format("shipp adding started"));

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
        log.info(String.format("shipp added"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Поездка успешно создана!"));
    }


    @PostMapping("/routedit")
    public ResponseEntity<?> editRouter(@RequestBody AddRoutRequest AddRequest) {

        log.info(String.format("shipp editing started"));

        Shipping ship = shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        log.info(String.format("ship id"+ship.getId()));
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
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
        log.info(String.format("shipp edited"));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Ship edited successfully!"));
    }

    @PostMapping("/refuseShip")
    public ResponseEntity<?> refuseShip(@RequestBody AddRoutRequest AddRequest) {
        log.info(String.format("refuse started"));
        Request rqst = rqstRepository.getOne(Long.parseLong(AddRequest.getId()));
        Shipping ship = shipRepository.getOne(rqst.getShippingId());
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
        rqst.setStatus(-1);
        rqstRepository.save(rqst);

        if(rqstRepository.findAllByShippingIdAndStatusIsNot(ship.getId(), -1).size()==0){
            log.info(String.format("no more"));
            ship.setStatus(Boolean.TRUE);
            shipRepository.save(ship);
        };

        return ResponseEntity.ok(new MessageResponse(" refuse suss "));
    }

    @PostMapping("/takeShip")
    public ResponseEntity<?> takeShip(@RequestBody AddRoutRequest AddRequest) {
        log.info(String.format("refuse started"));
        Request rqst = rqstRepository.getOne(Long.parseLong(AddRequest.getId()));
        Shipping ship = shipRepository.getOne(rqst.getShippingId());
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
        rqst.setStatus(1);
        rqstRepository.save(rqst);
        return ResponseEntity.ok(new MessageResponse(" taken suss "));
    }

    @PostMapping("/deleteShip")
    public ResponseEntity<?> deleteShip(@RequestBody AddRoutRequest AddRequest) {
        log.info(String.format("delete started"));
        Shipping ship=shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
        shipRepository.delete(ship);
        return ResponseEntity.ok(new MessageResponse(" delete suss "));
    }

    @GetMapping("/shippingsfiltered")
    @PreAuthorize("hasAuthority('client')")
    public ResponseEntity<?> getFilteredShippings(String startPoint, String finishPoint, String startDate, String finishDate,
                                                  Integer weight, Integer height, Integer width, Integer length) {
        log.info("Started filtering");

        try {
            return ResponseEntity.ok(shippingService.getFilteredShippings(startPoint, finishPoint, startDate, finishDate, weight, height, width, length));
        }
        catch (ParseException exception) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: wrong date format"));
        }
    }
}
