package com.makbe.ims.controller.order;

import com.makbe.ims.dto.order.OrderDto;
import com.makbe.ims.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/sales")
    public ResponseEntity<OrderDto> createSalesOrder(@RequestBody CreateOrderRequest request) {
        var order = orderService.createSalesOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/purchases")
    public ResponseEntity<OrderDto> createPurchasesOrder(@RequestBody CreateOrderRequest request) {
        var order = orderService.createPurchasesOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable String orderId, @RequestBody UpdateOrderStatusRequest request) {
        var order = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
