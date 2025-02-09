package orderbook.dataprovider.controllers;

import orderbook.domain.models.Wallet;
import orderbook.domain.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/customer/{id}")
    public Wallet findByCustomerId(@PathVariable Long id) {

        return walletService.findWalletByCustomerId(id);
    }
}
