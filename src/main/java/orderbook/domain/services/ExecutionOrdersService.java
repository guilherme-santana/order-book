package orderbook.domain.services;

import orderbook.domain.models.Order;
import orderbook.enuns.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ExecutionOrdersService {

    private final OrderService orderService;

    @Autowired
    public ExecutionOrdersService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void execute(Order order){
        if (orderMatch(order)){
            System.out.println("Deu match");
        }
    }

    private boolean orderMatch(Order order){
        List<Order> orders = orderService.findByOpenOrders();
        OrderType orderType = order.getOrderType();
        String assetName = order.getAsset().getName();
        Integer amount = order.getAmount();
        BigDecimal price = order.getPrice();

        for (Order ord : orders){
            if (Objects.equals(assetName, ord.getAsset().getName())
                    && orderType != ord.getOrderType()){

                if ( amount <= ord.getAmount() && Objects.equals(price, ord.getPrice())){
                    return true;
                }
            }
        }
        return false;
    }



}
