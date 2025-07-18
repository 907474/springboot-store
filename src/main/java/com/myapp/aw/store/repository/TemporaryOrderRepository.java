package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.TemporaryOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporaryOrderRepository extends JpaRepository<TemporaryOrder, Long> {

    Optional<TemporaryOrder> findByOrderIdAndStatus(Long orderId, OrderStatus status);

    Page<TemporaryOrder> findAllByStatus(OrderStatus status, Pageable pageable);

    List<TemporaryOrder> findAllByStatus(OrderStatus status);

    @Query("SELECT o FROM TemporaryOrder o WHERE o.status = :status AND (o.customerId = :searchId OR o.orderId = :searchId)")
    Page<TemporaryOrder> findByStatusAndCustomerIdOrOrderId(@Param("status") OrderStatus status, @Param("searchId") Long searchId, Pageable pageable);

    Page<TemporaryOrder> findAllByStatusAndCustomerId(OrderStatus status, Long customerId, Pageable pageable);
}