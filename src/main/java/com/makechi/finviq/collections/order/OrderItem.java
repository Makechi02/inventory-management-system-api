package com.makechi.finviq.collections.order;

import org.bson.types.ObjectId;

import java.math.BigDecimal;

public record OrderItem(ObjectId itemId, int quantity, BigDecimal price) {
}
