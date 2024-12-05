package orderbook.domain.services;

import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllOrders(){
            return orderRepository.findAll();
    }

    public Order findOrderById(Long id){
        return orderRepository.findById(id).orElseThrow();
    }

    public HttpEntity<OrderResponse> createNewOrder(Order order){
        Order orderCreated = orderRepository.save(order);
        OrderResponse response = new OrderResponse();

        response.setId(orderCreated.getId());
        response.setOrderType(orderCreated.getOrderType());
        response.setPrice(orderCreated.getPrice());
        response.setAmount(orderCreated.getAmount());
        response.setOrderStatus(orderCreated.getOrderStatus());
        response.setLocalDateTime(orderCreated.getLocalDateTime());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public Order updateOrder(Order order){
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id){
        orderRepository.deleteById(id);
    }

}
