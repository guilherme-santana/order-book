package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.PasswordRepository;
import orderbook.domain.messages.Messages;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordService(PasswordRepository passwordRepository, PasswordEncoder passwordEncoder) {
        this.passwordRepository = passwordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Password findPasswordById(Long id){
        return passwordRepository.findById(id).orElseThrow(() -> new BusinessException(Messages.CLIENTE_INVALIDO));
    }

    public Password findPasswordByCustomerId(Long customerId){
        return passwordRepository.findPasswordByCustomerId(customerId).orElseThrow(() -> new BusinessException(Messages.CLIENTE_INVALIDO));
    }

    public void createPassword(Customer customer, String password){
        Password passwordObj = new Password();
        passwordObj.setPassword(passwordEncoder.encode(password));
        passwordObj.setCustomer(customer);
        passwordRepository.save(passwordObj);
    }


}
