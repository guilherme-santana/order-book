package orderbook.dataprovider.controllers;

import jakarta.validation.Valid;
import orderbook.domain.models.Customer;
import orderbook.domain.services.CustomerRequest;
import orderbook.domain.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        customerService.createCustomer(customerRequest);
        log.info("M=registerCustomer, statusCode = {}", HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Cliente cadastrado com sucesso!");
    }
}
