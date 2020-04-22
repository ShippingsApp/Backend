package com.shippings.repositories;

import com.shippings.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserFromIdAndStatus(Long userFromId, Boolean bool);

    Request findOneById(Long ID);

}
