package com.makbe.ims.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StockLevelDto {
    private String name;
    private double quantity;
    private double stockAlert;
}
