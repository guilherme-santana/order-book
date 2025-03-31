package orderbook.dataprovider.controllers;

import jakarta.validation.Valid;
import orderbook.domain.models.Wallet;
import orderbook.domain.services.WalletRequest;
import orderbook.domain.services.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/customer/{id}")
    public Wallet findByCustomerId(@PathVariable Long id) {

        return walletService.findWalletByCustomerId(id);
    }

    @PostMapping("/apport")
    public ResponseEntity<?> capitalContribution(@Valid @RequestBody WalletRequest walletRequest) {
        var response = walletService.capitalContribution(walletRequest.getCustomerId(), walletRequest.getBalance());
        log.info("M=capitalContribution, statusCode = {}", HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
