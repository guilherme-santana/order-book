package orderbook.dataprovider.controllers;

import jakarta.validation.Valid;
import orderbook.domain.models.Order;
import orderbook.domain.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final AssetsService assetsService;

    @Autowired
    public OrderController(OrderService orderService, AssetsService assetsService) {
        this.orderService = orderService;
        this.assetsService = assetsService;
    }

    @GetMapping("/open")
    public List<Order> findByOpenOrders() {
        return orderService.findByOpenOrders();
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@Valid @RequestBody OrderRequest request) {
        var orderCreated = orderService.createNewOrder(request);
        var response = new OrderResponse();

        response.setId(orderCreated.getId());
        response.setOrderType(orderCreated.getOrderType());
        response.setPrice(orderCreated.getPrice());
        response.setAmount(orderCreated.getAmount());
        response.setOrderStatus(orderCreated.getOrderStatus());
        response.setLocalDateTime(orderCreated.getLocalDateTime());
        response.setAssetName(orderCreated.getAsset().getName());

        log.info("M=createNewOrder, statusCode = {}", HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderUpdateRequest orderRequest) {
        var orderUpdated = orderService.updateOrder(id, orderRequest);

        var response = new OrderResponse();
        response.setId(orderUpdated.getId());
        response.setOrderType(orderUpdated.getOrderType());
        response.setPrice(orderUpdated.getPrice());
        response.setAmount(orderUpdated.getAmount());
        response.setOrderStatus(orderUpdated.getOrderStatus());
        response.setLocalDateTime(orderUpdated.getLocalDateTime());
        response.setAssetName(orderUpdated.getAsset().getName());

        log.info("M=updateOrder, orderId = {}, statusCode = {}", id, HttpStatus.OK);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        log.info("M=cancelOrder, orderId = {}, statusCode = {}", id, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

}
