package orderbook.domain.services;

import orderbook.dataprovider.repositories.CustomerStockRepository;
import orderbook.domain.models.Asset;
import orderbook.domain.models.Customer;
import orderbook.domain.models.CustomerStock;
import orderbook.domain.models.Order;
import orderbook.exceptions.ExceptionOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerStockService {
    private static final Logger log = LoggerFactory.getLogger(CustomerStockService.class);

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
        log.info("M=createStockAsset, asset = {}, amount = {}, customerId = {}", customerStock.getAsset().getName(), customerStock.getAmount(), customerStock.getCustomer().getId());
        customerStockRepository.save(customerStock);
    }

    public void deleteStockAsset(Long customerId, Long assetId) {
        customerStockRepository.deleteById(findByCustomerAndAsset(customerId, assetId).getId());
    }

    public void createTransactionAskAsset(Order order) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());
        log.info("M=createTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

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
        log.info("M=createTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset().getName(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);

    }

    public void updateTransactionAskAsset(Order order, OrderRequest orderRequest) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());
        log.info("M=updateTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

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
        log.info("M=updateTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset().getName(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);

    }

    public void cancellTransactionAskAsset(Order order) {
        CustomerStock stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());
        log.info("M=cancellTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

        Integer actualAmount = stock.getAmount();
        Integer orderAmount = order.getAmount();

        Integer amount = actualAmount + orderAmount;
        stock.updateAmount(amount);
        log.info("M=cancellTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);
    }

    public void updateBuyerStockAsset(Customer buyer, Order order) {
        CustomerStock stockBuyer = findByCustomerAndAsset(buyer.getId(), order.getAsset().getId());

        if (stockBuyer == null) {
            createStockAsset(buyer, assetsService.findAssetsById(order.getAsset().getId()), order.getAmount());
        } else {
            int newAmount = stockBuyer.getAmount() + order.getAmount();
            stockBuyer.updateAmount(newAmount);
            log.info("M=updateBuyerStockAsset, asset = {}, newAmount = {}, customerId = {}", stockBuyer.getAsset().getName(), newAmount, stockBuyer.getCustomer().getId());

            customerStockRepository.save(stockBuyer);
        }
    }

}
