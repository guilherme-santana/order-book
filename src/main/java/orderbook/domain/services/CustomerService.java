package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.CustomerRepository;
import orderbook.domain.messages.Messages;
import orderbook.domain.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final PasswordService passwordService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, WalletService walletService, PasswordService passwordService) {
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.passwordService = passwordService;
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerByDocument(Integer document) {
        return customerRepository.findCustomerByDocument(document).orElse(null);
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email).orElseThrow(() -> new BusinessException(Messages.USARIO_SENHA_INVALIDO));
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(Messages.CLIENTE_INVALIDO));
    }

    public Customer findCustomerByName(String name) {
        return customerRepository.findCustomerByName(name)
                .orElseThrow(() -> new BusinessException(Messages.CLIENTE_INVALIDO));
    }

    public Customer createCustomer(CustomerRequest customerRequest) {
        if (findCustomerByDocument(customerRequest.getDocument()) == null && findCustomerByEmail(customerRequest.getEmail()) == null) {
            var customer = new Customer(customerRequest.getName(),customerRequest.getDocument(), customerRequest.getEmail());
            var customerCreated = customerRepository.save(customer);
            walletService.createWallet(customerCreated);
            passwordService.createPassword(customerCreated, customerRequest.getPassword());
            return customerCreated;
        } else {
            throw new BusinessException(Messages.CLIENTE_JA_CADASTRADO);
        }
    }


}
