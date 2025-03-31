package orderbook.config;

import orderbook.domain.models.Customer;
import orderbook.domain.services.CustomerService;
import orderbook.domain.services.PasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerService customerService;
    private final PasswordService passwordService;

    public CustomUserDetailsService(CustomerService customerService, PasswordService passwordService) {
        this.customerService = customerService;
        this.passwordService = passwordService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer user = customerService.findCustomerByEmail(username);
        var password = passwordService.findPasswordByCustomerId(user.getId()).getPassword();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(password)
                .roles("USER")
                .build();
    }
}
