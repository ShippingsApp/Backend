package com.shippings.repositories;

import com.shippings.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippRepository extends JpaRepository<Shipping, Long> {
    List<Shipping> findAllByDriverIdAndStatusOrderByDateStartDesc(Long driverId, Boolean bool);

    Shipping findOneById(Long ID);

}
