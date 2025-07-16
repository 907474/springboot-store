package com.myapp.aw.store.model.dto;

import com.myapp.aw.store.model.OrderArchive;
import java.math.BigDecimal;

public record OrderHistoryDTO(
        OrderArchive order,
        BigDecimal averageItemPrice,
        int totalItems
) {}