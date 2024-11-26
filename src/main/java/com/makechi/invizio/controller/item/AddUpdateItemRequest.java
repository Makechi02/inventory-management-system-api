package com.makechi.invizio.controller.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUpdateItemRequest {
    private String brand;
    private String category;
    private String model;
    private String name;
    private String sku;
    private double price;
    private double quantity;
    private int stockAlert;
}
