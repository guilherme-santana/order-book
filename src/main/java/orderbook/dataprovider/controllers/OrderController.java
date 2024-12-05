package orderbook.dataprovider.controllers;

import orderbook.domain.models.Book;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Order;
import orderbook.domain.services.OrderRequest;
import orderbook.domain.services.OrderResponse;
import orderbook.domain.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public List<Order> findAllOrders(){
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable Long id){
        return orderService.findOrderById(id);
    }

    @PostMapping
    public OrderResponse createNewOrder(@RequestBody OrderRequest request){
        Order order = new Order();
        order.setBook(new Book(request.getBookId()));
        order.setCustomer(new Customer(request.getCustomerId()));
        order.setOrderType(request.getOrderType());
        order.setPrice(request.getPrice());
        order.setAmount(request.getAmount());
        order.setOrderStatus(request.getOrderStatus());
        order.setLocalDateTime(LocalDateTime.now());

        return orderService.createNewOrder(order).getBody();
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order){
        order.setId(id);
        return orderService.updateOrder(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
    }

}
