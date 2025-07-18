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

    Page<OrderArchive> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT o FROM OrderArchive o WHERE o.customerId = :searchId OR o.orderId = :searchId")
    Page<OrderArchive> findByCustomerIdOrOrderId(@Param("searchId") Long searchId, Pageable pageable);
}