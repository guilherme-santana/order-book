package orderbook.domain.services;

import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Asset;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Order;

import orderbook.enuns.OrderType;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static orderbook.enuns.OrderStatus.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final AssetsService assetsService;
    private final WalletService walletService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerService customerService, AssetsService assetsService, WalletService walletService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.assetsService = assetsService;
        this.walletService = walletService;
    }

    public List<Order> findAllOrders(){
            return orderRepository.findAll();
    }

    public List<Order> findByOpenOrders(){
        return orderRepository.findByOpenOrders();
    }

    public Order findOrderById(Long id){
        return orderRepository.findById(id).orElseThrow();
    }

    public Order createNewOrder(OrderRequest orderRequest){
        Customer customer = customerService.findCustomerById(orderRequest.getCustomerId());
        Asset asset = assetsService.findAssetsById(orderRequest.getAssetId());

        if (customer == null){
            throw new ExceptionOrder("Cliente inválido");
        }
        if (asset == null){
            throw new ExceptionOrder("Ativo inválido");
        }

        Order order = new Order(
                customer,
                orderRequest.getOrderType(),
                orderRequest.getPrice(),
                orderRequest.getAmount(),
                PENDING,
                LocalDateTime.now(),
                asset
        );

        if(order.getOrderType().equals(OrderType.BIDS)){
            walletService.createTransactionBidWallet(order);
        }

        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderRequest orderRequest){
        Order order = findOrderById(id);

        if (order.getOrderStatus() != PENDING) {
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        if(order.getOrderType().equals(OrderType.BIDS)){
            walletService.updateTransactionBidWallet(order, orderRequest);
        }

        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());
        order.setLocalDateTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public void cancelOrder(Long id){
        Order order = findOrderById(id);

        if(order.getOrderStatus() != PENDING){
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        if(order.getOrderType().equals(OrderType.BIDS)) {
            walletService.cancellTransactionBidWallet(order);
        }

        order.setOrderStatus(CANCELED);
        order.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(order);
    }

}
