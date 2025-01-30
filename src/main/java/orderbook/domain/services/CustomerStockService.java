package orderbook.domain.services;

import orderbook.dataprovider.repositories.CustomerStockRepository;
import orderbook.domain.models.Asset;
import orderbook.domain.models.Customer;
import orderbook.domain.models.CustomerStock;
import orderbook.domain.models.Order;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerStockService {

    private final CustomerStockRepository customerStockRepository;
    private final AssetsService assetsService;


    @Autowired
    public CustomerStockService(CustomerStockRepository customerStockRepository, AssetsService assetsService) {
        this.customerStockRepository = customerStockRepository;
        this.assetsService = assetsService;
    }

    public List<CustomerStock> findCustomerStockByCustomerId(Long customerId) {
        return customerStockRepository.findByCustomerID(customerId);
    }

    public CustomerStock findByCustomerAndAsset(Long customerId, Long assetId) {
        return customerStockRepository.findByCustomerIdAndAssetId(customerId, assetId);
    }

    public void createStockAsset(Customer customer, Asset asset, Integer amount) {

        CustomerStock customerStock = new CustomerStock(customer, asset, amount);

        customerStockRepository.save(customerStock);
    }

    public void deleteStockAsset(Long customerId, Long assetId) {
        customerStockRepository.deleteById(findByCustomerAndAsset(customerId, assetId).getId());
    }

    public void createTransactionAskAsset(Order order) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        if (stock == null) {
            throw new ExceptionOrder("Ativo não encontrado!");
        }

        Integer actualAmount = stock.getAmount();
        Integer orderAmount = order.getAmount();

        if (actualAmount < orderAmount) {
            throw new ExceptionOrder("Quantidade de ativo não suficiente para realizar a operação!");
        }

        int amount = actualAmount - orderAmount;
        stock.updateAmount(amount);
        customerStockRepository.save(stock);

    }

    public void updateTransactionAskAsset(Order order, OrderRequest orderRequest) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        if (stock == null) {
            throw new ExceptionOrder("Ativo não encontrado!");
        }

        Integer realAmount = stock.getAmount() + order.getAmount();
        Integer newOrderAmount = orderRequest.getAmount();

        if (realAmount < newOrderAmount) {
            throw new ExceptionOrder("Quantidade de ativo não suficiente para realizar a operação!");
        }

        int amount = realAmount - newOrderAmount;
        stock.updateAmount(amount);
        customerStockRepository.save(stock);

    }

    public void cancellTransactionAskAsset(Order order) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        Integer actualAmount = stock.getAmount();
        Integer orderAmount = order.getAmount();

        Integer amount = actualAmount + orderAmount;
        stock.updateAmount(amount);

        customerStockRepository.save(stock);
    }

    public void updateBuyerStockAsset(Customer buyer, Order order) {
        CustomerStock stockBuyer = findByCustomerAndAsset(buyer.getId(), order.getAsset().getId());
        if (stockBuyer == null) {
            createStockAsset(buyer, assetsService.findAssetsById(order.getAsset().getId()), order.getAmount());
        } else {
            int newAmount = stockBuyer.getAmount() + order.getAmount();
            stockBuyer.updateAmount(newAmount);
            customerStockRepository.save(stockBuyer);
        }
    }

}
