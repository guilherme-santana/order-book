package orderbook.dataprovider.controllers;

import orderbook.domain.models.Book;
import orderbook.domain.models.Order;
import orderbook.domain.services.BookService;
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
    private final BookService bookService;

    @Autowired
    public OrderController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
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
            Book bookName = bookService.findBookById(orderCreated.getBook().getId());
            response.setBookName(bookName.getName());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            throw new ExceptionOrder("Erro ao tentar criar nova ordem!");
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
            Book bookName = bookService.findBookById(orderUpdated.getBook().getId());
            response.setBookName(bookName.getName());

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
