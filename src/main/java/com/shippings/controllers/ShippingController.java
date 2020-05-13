package com.shippings.controllers;

import com.shippings.model.*;
import com.shippings.payload.request.*;
import com.shippings.payload.response.*;
import com.shippings.repositories.*;
import com.shippings.security.services.UserDetailsImpl;
import com.shippings.services.ShippingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ship")
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
     public List<Map<String, String>> driverAccess(final Integer time_per){
        return getRouteList(Boolean.TRUE, time_per);
    }

    @GetMapping("/driverRequest")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> driverRequestAccess(final Integer status) {
       return getRouteShippedList(status);
    }

    List<Map<String, String>> getRouteShippedList(final int status){

        List<Shipping> shipps = shipRepository.findAllByDriverIdAndStatusOrderByDateStartDesc(this.getCurrentUserId(), Boolean.FALSE);
        for (Iterator<Shipping> iter = shipps.listIterator(); iter.hasNext(); ) {
            Shipping a = iter.next();
            if (rqstRepository.findAllByShippingIdAndStatus(a.getId(),status).size()==0){
                iter.remove();
            }
        }

        return toListMap(shipps);
    }

    List<Map<String, String>> getRouteList(final Boolean bool, final int time_per){
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
        return toListMap(shipps);
        }

    List<Map<String, String>> toListMap(List<Shipping> shipps){
        List<Map<String, String>> shippList = new ArrayList();

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
    @PreAuthorize("hasAnyAuthority('driver', 'client')")
    public Shipping getRoute( final long ID){
       Shipping shipp = shipRepository.findOneById(ID);

        return shipp;
    }

    @GetMapping("/getShipRequests")
    @PreAuthorize("hasAuthority('driver')")
    public List<Map<String, String>> getShipRequests(final Long ID, final Integer status){
        if(shipRepository.getOne(ID).getDriverId()!=getCurrentUserId()){return null;}
        List<Map<String, String>> rqstList = new ArrayList();
        List<Request> rqst = rqstRepository.findAllByShippingIdAndStatus(ID,status);

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
    public ResponseEntity<?> addRouter(@RequestBody final AddRoutRequest AddRequest) {

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
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Поездка успешно создана!"));
    }

    @PostMapping("/routedit")
    public ResponseEntity<?> editRouter(@RequestBody final AddRoutRequest AddRequest) {

        Shipping ship = shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("You don't have access!"));}
        Optional.ofNullable(AddRequest.getDateStart()).ifPresent(val -> ship.setDateStart(Date.valueOf(val)));
        Optional.ofNullable(AddRequest.getDateFinish()).ifPresent(val -> ship.setDateFinish(Date.valueOf(val)));
        Optional.ofNullable(AddRequest.getStart()).ifPresent(val -> ship.setStart(val));
        Optional.ofNullable(AddRequest.getFinish()).ifPresent(val -> ship.setFinish(val));
        Optional.ofNullable(AddRequest.getFinish()).ifPresent(val -> ship.setFinish(val));
        Optional.ofNullable(AddRequest.getWeight()).ifPresent(val -> ship.setWeight(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getHeight()).ifPresent(val -> ship.setHeight(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getLength()).ifPresent(val -> ship.setLength(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getWidth()).ifPresent(val -> ship.setWidth(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getPlusTime()).ifPresent(val -> ship.setPlusTime(Integer.parseInt(val)));
        Optional.ofNullable(AddRequest.getComment()).ifPresent(val -> ship.setComment(val));
        shipRepository.save(ship);

        return ResponseEntity.ok(new MessageResponse("Маршрут успешно добавлен!"));
    }

    @PostMapping("/refuseShip")
    public ResponseEntity<?> refuseShip(@RequestBody final AddRoutRequest AddRequest) {
        Request rqst = rqstRepository.getOne(Long.parseLong(AddRequest.getId()));
        Shipping ship = shipRepository.getOne(rqst.getShippingId());
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}
        rqst.setStatus(-1);
        rqstRepository.save(rqst);

        if(rqstRepository.findAllByShippingIdAndStatusIsNot(ship.getId(), -1).size()==0){
            ship.setStatus(Boolean.TRUE);
            shipRepository.save(ship);
        };

        return ResponseEntity.ok(new MessageResponse(" refuse suss "));
    }

    @PostMapping("/takeShip")
    public ResponseEntity<?> takeShip(@RequestBody final AddRoutRequest AddRequest) {
        Request rqst = rqstRepository.getOne(Long.parseLong(AddRequest.getId()));
        Shipping ship = shipRepository.getOne(rqst.getShippingId());
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}
        rqst.setStatus(1);
        rqstRepository.save(rqst);
        return ResponseEntity.ok(new MessageResponse(" Успешно принято "));
    }

    @PostMapping("/deleteShip")
    public ResponseEntity<?> deleteShip(@RequestBody final AddRoutRequest AddRequest) {
        Shipping ship=shipRepository.getOne(Long.parseLong(AddRequest.getId()));
        if(ship.getDriverId()!=getCurrentUserId()){return ResponseEntity.ok(new MessageResponse("В доступе отказано!"));}
        shipRepository.delete(ship);
        return ResponseEntity.ok(new MessageResponse(" Успешно удалено "));
    }

    @GetMapping("/shippingsfiltered")
    @PreAuthorize("hasAuthority('client')")
    public ResponseEntity<?> getFilteredShippings(String startPoint, String finishPoint, String startDate, String finishDate,
                                                  Integer weight, Integer height, Integer width, Integer length) {

        try {
            return ResponseEntity.ok(shippingService.getFilteredShippings(startPoint, finishPoint, startDate, finishDate, weight, height, width, length));
        }
        catch (ParseException exception) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ошибка: неправильный формат даты"));
        }
    }
}
