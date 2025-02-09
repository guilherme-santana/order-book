package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Order;
import orderbook.enuns.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static orderbook.domain.messages.Messages.*;
import static orderbook.enuns.OrderStatus.*;


@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

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

    public List<Order> findOrdersOpenValidForMatch(Long customerId, OrderType orderType, BigDecimal price, Long assetId) {
        return orderRepository.findByOpenValidForMatchOrders(customerId, orderType, price, assetId);
    }

    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NENHUMA_ORDEM_ENCONTRADA));
    }

    public Order createNewOrder(OrderRequest orderRequest) {
        log.info("M=createNewOrder, assetId = {}, customerId = {}, orderType = {}, price = {}, amount = {} ",
                orderRequest.getAssetId(),
                orderRequest.getCustomerId(),
                orderRequest.getOrderType(),
                orderRequest.getPrice(),
                orderRequest.getAmount()
        );

        var customer = customerService.findCustomerById(orderRequest.getCustomerId());
        var asset = assetsService.findAssetsById(orderRequest.getAssetId());

        var order = new Order(
                customer,
                orderRequest.getOrderType(),
                orderRequest.getPrice(),
                orderRequest.getAmount(),
                PENDING,
                LocalDateTime.now(),
                asset
        );

        if (OrderType.BIDS.equals(order.getOrderType())) {
            walletService.createTransactionBidWallet(order);
        } else if (OrderType.ASKS.equals(order.getOrderType())) {
            customerStockService.createTransactionAskAsset(order);
        }

        var orderValidMatch = findOrdersOpenValidForMatch(
                order.getCustomer().getId(),
                order.getOrderType(),
                order.getPrice(),
                order.getAsset().getId());

        return orderRepository.save(executeOrder(orderValidMatch,order));
    }

    public Order updateOrder(Long id, OrderUpdateRequest orderRequest) {
        var order = findOrderById(id);
        log.info("M=updateOrder, assetId = {}, customerId = {}, orderType = {}, price = {}, amount = {} ",
                order.getAsset().getId(),
                order.getCustomer().getId(),
                order.getOrderType(),
                order.getPrice(),
                order.getAmount()
        );
        if (order.getOrderStatus() != PENDING) {
            throw new BusinessException(ORDENS_EXECUTADAS_OU_CANCELADAS_NAO_PODEM_SER_ALTERADAS);
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

    public Map<String, String> cancelOrder(Long id) {
        var order = findOrderById(id);

        if (order.getOrderStatus() != PENDING) {
            throw new BusinessException(ORDENS_EXECUTADAS_OU_CANCELADAS_NAO_PODEM_SER_ALTERADAS);
        }

        if (order.getOrderType().equals(OrderType.BIDS)) {
            walletService.cancellTransactionBidWallet(order);
        } else if (order.getOrderType().equals(OrderType.ASKS)) {
            customerStockService.cancellTransactionAskAsset(order);
        }

        order.setOrderStatus(CANCELED);
        order.setLocalDateTime(LocalDateTime.now());
        log.info("M=cancelOrder, status = {}", order.getOrderStatus());
        orderRepository.save(order);

        Map<String, String> response = new HashMap<>();
        response.put("message", ORDEM_CANCELADA_COM_SUCESSO);
        response.put("statusCode", String.valueOf(HttpStatus.OK));

        return response;

    }

    private Order executeOrder(List<Order> ordersMatch, Order orderInExecution) {
        var originalAmountOrder = orderInExecution.getAmount();
        if (!ordersMatch.isEmpty()) {

            for (Order orderMatch : ordersMatch) {

                if (orderInExecution.getOrderStatus() == PENDING) {

                    if (orderInExecution.getAmount() > orderMatch.getAmount()) {
                        executeOrderWhenOrderInExecutionIsGreaterThanOrderMatch(orderMatch, orderInExecution);
                    } else if (orderInExecution.getAmount() < orderMatch.getAmount()) {
                        executeOrderWhenOrderInExecutionIsLessThanOrderMatch(orderMatch, orderInExecution);
                    } else {
                        executeOrderWhenOrderInExecutionAndOrderMatchIsSatisfactory(orderMatch, orderInExecution);
                    }

                    if (orderInExecution.getOrderType().equals(OrderType.BIDS)) {
                        walletService.updateSellerWallet(orderMatch.getCustomer().getId(), orderInExecution);
                        customerStockService.updateBuyerStockAsset(orderInExecution.getCustomer(), orderInExecution);
                    } else {
                        walletService.updateSellerWallet(orderInExecution.getCustomer().getId(), orderInExecution);
                        customerStockService.updateBuyerStockAsset(orderMatch.getCustomer(), orderInExecution);
                    }
                }
            }

        }
        if (orderInExecution.getOrderStatus() == EXECUTED) {
            orderInExecution.setAmount(originalAmountOrder);
        }
        return orderInExecution;
    }

    private void executeOrderWhenOrderInExecutionAndOrderMatchIsSatisfactory(Order ordersMatch, Order orderInExecution) {
        log.info("M=executeOrderWhenOrderInExecutionAndOrderMatchIsSatisfactory, ordersMatchId = {}, orderInExecutionId = {}", ordersMatch.getId(), orderInExecution.getId());
        ordersMatch.setOrderStatus(EXECUTED);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderInExecution.setOrderStatus(EXECUTED);
        orderInExecution.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(orderInExecution);

    }

    private void executeOrderWhenOrderInExecutionIsGreaterThanOrderMatch(Order ordersMatch, Order orderInExecution) {
        log.info("M=executeOrderWhenOrderInExecutionIsGreaterThanOrderMatch, ordersMatchId = {}, orderInExecutionId = {}", ordersMatch.getId(), orderInExecution.getId());
        int amountExecution = orderInExecution.getAmount() - ordersMatch.getAmount();
        orderInExecution.setAmount(amountExecution);
        ordersMatch.setOrderStatus(EXECUTED);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderRepository.save(orderInExecution);

    }

    private void executeOrderWhenOrderInExecutionIsLessThanOrderMatch(Order ordersMatch, Order orderInExecution) {
        log.info("M=executeOrderWhenOrderInExecutionIsLessThanOrderMatch, ordersMatchId = {}, orderInExecutionId = {}", ordersMatch.getId(), orderInExecution.getId());
        int amountMatch = ordersMatch.getAmount() - orderInExecution.getAmount();
        ordersMatch.setAmount(amountMatch);
        ordersMatch.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(ordersMatch);
        orderInExecution.setOrderStatus(EXECUTED);
        orderInExecution.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(orderInExecution);

    }

}
