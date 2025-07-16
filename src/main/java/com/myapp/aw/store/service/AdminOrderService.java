package com.myapp.aw.store.service;

import com.myapp.aw.store.model.ArchivedProductItem;
import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.TemporaryOrder;
import com.myapp.aw.store.model.dto.OrderDisplayDTO;
import com.myapp.aw.store.model.dto.OrderHistoryDTO;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminOrderService {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderService.class);

    private final TemporaryOrderRepository temporaryOrderRepository;
    private final OrderArchiveRepository orderArchiveRepository;
    private final ProductRepository productRepository;

    public record OrderItemDetailDTO(String productName, int quantity, BigDecimal priceAtPurchase) {}

    public AdminOrderService(TemporaryOrderRepository temporaryOrderRepository, OrderArchiveRepository orderArchiveRepository, ProductRepository productRepository) {
        this.temporaryOrderRepository = temporaryOrderRepository;
        this.orderArchiveRepository = orderArchiveRepository;
        this.productRepository = productRepository;
    }

    // Simplified to return a full list
    public List<OrderDisplayDTO> getCombinedOrders(String statusFilter) {
        log.info("Fetching combined orders with filter: {}", statusFilter);
        Stream<OrderDisplayDTO> unfinishedOrders = Stream.empty();
        Stream<OrderDisplayDTO> finishedOrders = Stream.empty();

        if (statusFilter == null || "IN_PROGRESS".equals(statusFilter)) {
            unfinishedOrders = temporaryOrderRepository.findAllByStatus(OrderStatus.IN_PROGRESS).stream().map(this::toDto);
        }

        if (statusFilter == null || "FINISHED".equals(statusFilter)) {
            finishedOrders = orderArchiveRepository.findAll().stream().map(this::toDto);
        }

        return Stream.concat(unfinishedOrders, finishedOrders)
                .sorted(Comparator.comparing(OrderDisplayDTO::orderDate).reversed())
                .toList();
    }

    // Simplified to return a full list
    public List<OrderHistoryDTO> getCustomerOrderHistory(Long customerId, Pageable pageable) {
        List<OrderArchive> orders = orderArchiveRepository.findByCustomerId(customerId);

        return orders.stream().map(order -> {
            int totalItems = order.getProductItems().stream()
                    .mapToInt(ArchivedProductItem::getQuantity)
                    .sum();

            BigDecimal averagePrice = BigDecimal.ZERO;
            if (totalItems > 0 && order.getTotalPrice() != null) {
                averagePrice = order.getTotalPrice().divide(new BigDecimal(totalItems), 2, RoundingMode.HALF_UP);
            }

            return new OrderHistoryDTO(order, averagePrice, totalItems);
        }).toList();
    }

    public List<OrderItemDetailDTO> getOrderDetails(String displayId) {
        log.info("Fetching details for order display ID: {}", displayId);
        char type = displayId.charAt(0);
        Long id = Long.parseLong(displayId.substring(2));
        List<Long> productIds;
        List<OrderItemDetailDTO> items = new ArrayList<>();

        if (type == 'T') {
            TemporaryOrder order = temporaryOrderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            productIds = order.getProductItems().stream().map(item -> item.getProductId()).toList();
            Map<Long, String> productNames = getProductNames(productIds);
            order.getProductItems().forEach(item -> items.add(new OrderItemDetailDTO(productNames.get(item.getProductId()), item.getQuantity(), item.getPriceAtPurchase())));
        } else if (type == 'F') {
            OrderArchive order = orderArchiveRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            productIds = order.getProductItems().stream().map(item -> item.getProductId()).toList();
            Map<Long, String> productNames = getProductNames(productIds);
            order.getProductItems().forEach(item -> items.add(new OrderItemDetailDTO(productNames.get(item.getProductId()), item.getQuantity(), item.getPriceAtPurchase())));
        }
        return items;
    }

    private Map<Long, String> getProductNames(List<Long> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Product::getProductName));
    }

    private OrderDisplayDTO toDto(TemporaryOrder order) { return new OrderDisplayDTO("T-" + order.getOrderId(), order.getCustomerId(), order.getTotalPrice(), order.getOrderCreationTime(), "In Progress"); }
    private OrderDisplayDTO toDto(OrderArchive order) { return new OrderDisplayDTO("F-" + order.getOrderId(), order.getCustomerId(), order.getTotalPrice(), order.getOrderPlacementTime(), "Finished"); }
}