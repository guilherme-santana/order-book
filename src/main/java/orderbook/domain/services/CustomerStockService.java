package orderbook.domain.services;

import orderbook.dataprovider.repositories.CustomerStockRepository;
import orderbook.domain.models.CustomerStock;
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


}
