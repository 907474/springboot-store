package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.OrderArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderArchiveRepository extends JpaRepository<OrderArchive, Long> {

    List<OrderArchive> findByCustomerId(Long customerId);

    List<OrderArchive> findByOrderPlacementTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM OrderArchive o LEFT JOIN FETCH o.productItems WHERE o.customerId = :customerId ORDER BY o.orderPlacementTime DESC")
    Page<OrderArchive> findByCustomerIdWithItems(@Param("customerId") Long customerId, Pageable pageable);
}