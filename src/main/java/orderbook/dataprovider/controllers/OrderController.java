package orderbook.dataprovider.controllers;

import orderbook.domain.models.Order;
import orderbook.domain.services.OrderRequest;
import orderbook.domain.services.OrderService;
import orderbook.exceptions.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/open")
    public List<Order> findByOpenOrders() {
        return orderService.findByOpenOrders();
    }

    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@RequestBody OrderRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderService.createNewOrder(request));
        } catch (Exception e) {
            throw new ExceptionHandler("Erro ao tentar criar nova ordem!");
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(orderService.updateOrder(id, orderRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
