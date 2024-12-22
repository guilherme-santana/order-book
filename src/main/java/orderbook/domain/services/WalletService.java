package orderbook.domain.services;

import orderbook.dataprovider.repositories.BookRepository;
import orderbook.dataprovider.repositories.WalletRepository;
import orderbook.domain.models.Book;
import orderbook.domain.models.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet findWalletByCustomerId(Long id){
            return walletRepository.findByCustomerID(id);
    }





}
