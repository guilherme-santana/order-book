package orderbook.domain.services;

import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Asset;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Order;
import orderbook.enuns.OrderType;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static orderbook.enuns.OrderStatus.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final AssetsService assetsService;
    private final WalletService walletService;
    private final CustomerStockService customerStockService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerService customerService, AssetsService assetsService, WalletService walletService, CustomerStockService customerStockService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.assetsService = assetsService;
        this.walletService = walletService;
        this.customerStockService = customerStockService;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findByOpenOrders() {
        return orderRepository.findByOpenOrders();
    }

    public List<Order> findOrdersOpenValidForMatch(Long customerId, OrderType orderType, BigDecimal price) {
        return orderRepository.findByOpenValidForMatchOrders(customerId, orderType, price);
    }

    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public Order createNewOrder(OrderRequest orderRequest) {
        Customer customer = customerService.findCustomerById(orderRequest.getCustomerId());
        Asset asset = assetsService.findAssetsById(orderRequest.getAssetId());

        if (customer == null) {
            throw new ExceptionOrder("Cliente inválido");
        }
        if (asset == null) {
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

        if (order.getOrderType().equals(OrderType.BIDS)) {
            walletService.createTransactionBidWallet(order);
        } else if (order.getOrderType().equals(OrderType.ASKS)) {
            customerStockService.createTransactionAskAsset(order);
        }

        return orderRepository.save(executeOrder(
                findOrdersOpenValidForMatch(
                        order.getCustomer().getId(),
                        order.getOrderType(),
                        order.getPrice()),
                order));
    }

    public Order updateOrder(Long id, OrderRequest orderRequest) {
        Order order = findOrderById(id);

        if (order.getOrderStatus() != PENDING) {
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        if (order.getOrderType().equals(OrderType.BIDS)) {
            walletService.updateTransactionBidWallet(order, orderRequest);
        } else if (order.getOrderType().equals(OrderType.ASKS)) {
            customerStockService.updateTransactionAskAsset(order, orderRequest);
        }

        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());
        order.setLocalDateTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        Order order = findOrderById(id);

        if (order.getOrderStatus() != PENDING) {
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        if (order.getOrderType().equals(OrderType.BIDS)) {
            walletService.cancellTransactionBidWallet(order);
        } else if (order.getOrderType().equals(OrderType.ASKS)) {
            customerStockService.cancellTransactionAskAsset(order);
        }

        order.setOrderStatus(CANCELED);
        order.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    private Order executeOrder(List<Order> ordersMatch, Order orderInExecution) {
        int originalAmountOrder = orderInExecution.getAmount();
        if (!ordersMatch.isEmpty()) {

            for (Order orderMatch : ordersMatch) {

                if (orderInExecution.getOrderStatus() == PENDING) {

                    if (orderMatch.getAmount() - orderInExecution.getAmount() == 0) {
                        executeOrderWhenOrderInExecutionAndOrderMatchIsSatisfactory(orderMatch, orderInExecution);
                    }

                    if (orderInExecution.getAmount() > orderMatch.getAmount()) {
                        executeOrderWhenOrderInExecutionIsGreaterThanOrderMatch(orderMatch, orderInExecution);

                    } else if (orderInExecution.getAmount() < orderMatch.getAmount()) {
                        executeOrderWhenOrderInExecutionIsLessThanOrderMatch(orderMatch, orderInExecution);
                    }
                }
                if (orderInExecution.getOrderType().equals(OrderType.BIDS)) {
                    walletService.updateSellerWallet(orderMatch.getCustomer().getId(), orderInExecution);
                    customerStockService.updateBuyerStockAsset(orderInExecution.getCustomer(), orderInExecution);
                } else {
                    walletService.updateSellerWallet(orderInExecution.getCustomer().getId(), orderMatch);
                    customerStockService.updateBuyerStockAsset(orderMatch.getCustomer(), orderMatch);
                }
            }

        }
        if (orderInExecution.getOrderStatus() == EXECUTED) {
            orderInExecution.setAmount(originalAmountOrder);
        }
        return orderInExecution;
    }

    private void executeOrderWhenOrderInExecutionAndOrderMatchIsSatisfactory(Order ordersMatch, Order orderInExecution) {

        ordersMatch.setOrderStatus(EXECUTED);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderInExecution.setOrderStatus(EXECUTED);
        orderInExecution.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(orderInExecution);

    }

    private void executeOrderWhenOrderInExecutionIsGreaterThanOrderMatch(Order ordersMatch, Order orderInExecution) {

        int amountExecution = orderInExecution.getAmount() - ordersMatch.getAmount();
        orderInExecution.setAmount(amountExecution);
        ordersMatch.setOrderStatus(EXECUTED);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderRepository.save(orderInExecution);

    }

    private void executeOrderWhenOrderInExecutionIsLessThanOrderMatch(Order ordersMatch, Order orderInExecution) {

        int amountMatch = ordersMatch.getAmount() - orderInExecution.getAmount();
        ordersMatch.setAmount(amountMatch);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderInExecution.setOrderStatus(EXECUTED);
        orderInExecution.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(orderInExecution);

    }

}
