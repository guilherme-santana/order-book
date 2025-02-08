package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.CustomerStockRepository;
import orderbook.domain.models.Asset;
import orderbook.domain.models.Customer;
import orderbook.domain.models.CustomerStock;
import orderbook.domain.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static orderbook.dataprovider.exceptions.Messages.ATIVO_INVALIDO;
import static orderbook.dataprovider.exceptions.Messages.QUANTIDADE_DE_ATIVO_NAO_SUFICIENTE_PARA_REALIZAR_A_OPERACAO;

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
        return customerStockRepository.findByCustomerID(customerId)
                .orElseThrow(() -> new BusinessException(ATIVO_INVALIDO));
    }

    public CustomerStock findByCustomerAndAsset(Long customerId, Long assetId) {
        return customerStockRepository.findByCustomerIdAndAssetId(customerId, assetId)
                .orElseThrow(() -> new BusinessException(ATIVO_INVALIDO));
    }

    public CustomerStock createStockAsset(Customer customer, Asset asset, Integer amount) {
        var customerStock = new CustomerStock(customer, asset, amount);
        log.info("M=createStockAsset, asset = {}, amount = {}, customerId = {}", customerStock.getAsset().getName(), customerStock.getAmount(), customerStock.getCustomer().getId());
        customerStockRepository.save(customerStock);
        return customerStock;
    }

    public void deleteStockAsset(Long customerId, Long assetId) {
        customerStockRepository.deleteById(findByCustomerAndAsset(customerId, assetId).getId());
    }

    public void createTransactionAskAsset(Order order) {
        var stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());


        log.info("M=createTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

        var actualAmount = stock.getAmount();
        var orderAmount = order.getAmount();

        if (actualAmount < orderAmount) {
            throw new BusinessException(QUANTIDADE_DE_ATIVO_NAO_SUFICIENTE_PARA_REALIZAR_A_OPERACAO);
        }

        var amount = actualAmount - orderAmount;
        stock.updateAmount(amount);
        log.info("M=createTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset().getName(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);

    }

    public void updateTransactionAskAsset(Order order, OrderUpdateRequest orderRequest) {
        var stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        log.info("M=updateTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

        var realAmount = stock.getAmount() + order.getAmount();
        var newOrderAmount = orderRequest.getAmount();

        if (realAmount < newOrderAmount) {
            throw new BusinessException(QUANTIDADE_DE_ATIVO_NAO_SUFICIENTE_PARA_REALIZAR_A_OPERACAO);
        }

        var amount = realAmount - newOrderAmount;
        stock.updateAmount(amount);
        log.info("M=updateTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset().getName(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);

    }

    public void cancellTransactionAskAsset(Order order) {
        var stock = findByCustomerAndAsset(order.getCustomer().getId(), order.getAsset().getId());

        log.info("M=cancellTransactionAskAsset, asset = {}, amount = {}, customerId = {}", stock.getAsset().getName(), stock.getAmount(), stock.getCustomer().getId());

        var actualAmount = stock.getAmount();
        var orderAmount = order.getAmount();

        var amount = actualAmount + orderAmount;
        stock.updateAmount(amount);
        log.info("M=cancellTransactionAskAsset, asset = {}, newAmount = {}, customerId = {}", stock.getAsset(), amount, stock.getCustomer().getId());

        customerStockRepository.save(stock);
    }

    public void updateBuyerStockAsset(Customer buyer, Order order) {
        var stockBuyer = findByCustomerAndAsset(buyer.getId(), order.getAsset().getId());

        Optional.ofNullable(stockBuyer).orElse(
                createStockAsset(
                        buyer,
                        order.getAsset(),
                        order.getAmount()
                )
        );

        var newAmount = stockBuyer.getAmount() + order.getAmount();
        stockBuyer.updateAmount(newAmount);
        log.info("M=updateBuyerStockAsset, asset = {}, newAmount = {}, customerId = {}", stockBuyer.getAsset().getName(), newAmount, stockBuyer.getCustomer().getId());

        customerStockRepository.save(stockBuyer);
    }

}
