package orderbook.dataprovider.controllers;

import orderbook.domain.models.Asset;
import orderbook.domain.models.Order;
import orderbook.domain.services.AssetsService;
import orderbook.domain.services.OrderRequest;
import orderbook.domain.services.OrderResponse;
import orderbook.domain.services.OrderService;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final AssetsService assetsService;

    @Autowired
    public OrderController(OrderService orderService, AssetsService assetsService) {
        this.orderService = orderService;
        this.assetsService = assetsService;
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
            Order orderCreated = orderService.createNewOrder(request);
            OrderResponse response = new OrderResponse();

            response.setId(orderCreated.getId());
            response.setOrderType(orderCreated.getOrderType());
            response.setPrice(orderCreated.getPrice());
            response.setAmount(orderCreated.getAmount());
            response.setOrderStatus(orderCreated.getOrderStatus());
            response.setLocalDateTime(orderCreated.getLocalDateTime());
            Asset bookName = assetsService.findAssetsById(orderCreated.getAsset().getId());
            response.setAssetName(bookName.getName());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        try {
            Order orderUpdated = orderService.updateOrder(id, orderRequest);

            OrderResponse response = new OrderResponse();
            response.setId(orderUpdated.getId());
            response.setOrderType(orderUpdated.getOrderType());
            response.setPrice(orderUpdated.getPrice());
            response.setAmount(orderUpdated.getAmount());
            response.setOrderStatus(orderUpdated.getOrderStatus());
            response.setLocalDateTime(orderUpdated.getLocalDateTime());
            Asset assetName = assetsService.findAssetsById(orderUpdated.getAsset().getId());
            response.setAssetName(assetName.getName());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
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
