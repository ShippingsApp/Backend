package com.shippings.repositories;

import com.shippings.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserFromIdAndStatus(Long userFromId, Integer status);
    List<Request> findAllByShippingIdAndStatusIsNot(Long Id, int status);
    List<Request> findAllByShippingIdAndStatus(Long Id, int status);
    Request findOneById(Long ID);
    List<Request> findAllByShippingId(long id);
}
