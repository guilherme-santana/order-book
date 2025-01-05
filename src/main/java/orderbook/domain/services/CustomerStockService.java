package orderbook.domain.services;

import orderbook.dataprovider.repositories.CustomerStockRepository;
import orderbook.domain.models.CustomerStock;
import orderbook.domain.models.Order;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerStockService {

    private final CustomerStockRepository customerStockRepository;


    @Autowired
    public CustomerStockService(CustomerStockRepository customerStockRepository) {
        this.customerStockRepository = customerStockRepository;
    }

    public List<CustomerStock> findCustomerStockByCustomerId(Long customerId){
        return customerStockRepository.findByCustomerID(customerId);
    }
    
    public CustomerStock findByCustomerAndAsset(Long customerId, Long assetId){
        return customerStockRepository.findByCustomerIdAndAssetId(customerId, assetId);
    }
    
    public void createTransactionAskAsset(Order order){
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        if(stock == null){
            throw new ExceptionOrder("Ativo não encontrado!");
        }

        Integer actualAmount = stock.getAmount();
        Integer orderAmount = order.getAmount();

        if(actualAmount < orderAmount) {
            throw new ExceptionOrder("Quantidade de ativo não suficiente para realizar a operação!");
        }

        Integer amount = actualAmount - orderAmount;
        stock.setAmount(amount);

        customerStockRepository.save(stock);
    }

    public void updateTransactionAskAsset(Order order, OrderRequest orderRequest){
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        if(stock == null){
            throw new ExceptionOrder("Ativo não encontrado!");
        }

        Integer realAmount = stock.getAmount() + order.getAmount();
        Integer newOrderAmount = orderRequest.getAmount();

        if(realAmount < newOrderAmount) {
            throw new ExceptionOrder("Quantidade de ativo não suficiente para realizar a operação!");
        }

        Integer amount = realAmount - newOrderAmount;
        stock.setAmount(amount);

        customerStockRepository.save(stock);
    }

    public void cancellTransactionAskAsset(Order order){
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        Integer actualAmount = stock.getAmount();
        Integer orderAmount = order.getAmount();

        Integer amount = actualAmount + orderAmount;
        stock.setAmount(amount);

        customerStockRepository.save(stock);
    }

}
