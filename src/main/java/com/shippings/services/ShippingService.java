package com.shippings.services;

import com.shippings.model.Shipping;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ShippingService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Shipping> getFilteredShippings(String startPoint, String finishPoint, String startDate, String finishDate,
                                               Integer weight, Integer height, Integer width, Integer length) throws ParseException {


        StringBuilder queryString = new StringBuilder();
        queryString.append("select sh from Shipping sh where ");
        queryString.append("(sh.start = '" + startPoint + " ' or sh.finish = '" + finishPoint + " ' or sh.start ='" + finishPoint + " ' or sh.finish ='" + startPoint + " ')");


        if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate boundStartLeft = LocalDate.parse(startDate, formatter);
            boundStartLeft.minusDays(7);
            queryString.append(" and sh.dateStart > '" + boundStartLeft.toString() + "'");
        }
        if (finishDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate boundStartRight = LocalDate.parse(finishDate, formatter);
            boundStartRight.plusDays(7);
            queryString.append(" and sh.dateFinish < '" + boundStartRight.toString() + "'");
        }
        if (weight != null) {
            queryString.append(" and sh.weight >= " + weight);
        }
        if (length != null) {
            queryString.append(" and sh.length >= " + length);
        }
        if (width != null) {
            queryString.append(" and sh.width >= " + width);
        }
        if (height != null) {
            queryString.append(" and sh.length >= " + height);
        }

        Query query = entityManager.createQuery(queryString.toString());

        return query.getResultList();
    }
}
