package orderbook.dataprovider.controllers;

import orderbook.domain.models.CustomerStock;
import orderbook.domain.services.CustomerStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class CustomerStockController {

    private final CustomerStockService customerStockService;

    @Autowired
    public CustomerStockController(CustomerStockService customerStockService) {
        this.customerStockService = customerStockService;
    }

    @GetMapping("/customer/{id}")
    public List<CustomerStock> findByCustomerId(@PathVariable Long id) {
        return customerStockService.findCustomerStockByCustomerId(id);
    }
}
