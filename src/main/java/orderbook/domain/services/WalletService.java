package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.WalletRepository;
import orderbook.domain.models.Order;
import orderbook.domain.models.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static orderbook.domain.messages.Messages.DADO_NAO_ENCONTRADO;
import static orderbook.domain.messages.Messages.SALDO_INSUFICIENTE;

@Service
public class WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet findWalletByCustomerId(Long id){
            return walletRepository.findByCustomerID(id)
                    .orElseThrow(() -> new IllegalArgumentException(DADO_NAO_ENCONTRADO));
    }


    public void createTransactionBidWallet(Order order){
        var wallet = findWalletByCustomerId(order.getCustomer().getId());
        log.info("M=createTransactionBidWallet, balance = {}, customerId = {}", wallet.getBalance(), wallet.getCustomer().getId());
        var actualBalance = wallet.getBalance();
        var amount = order.getAmount();
        var orderPrice = order.getPrice().multiply(BigDecimal.valueOf(amount)); // order

        if(actualBalance.compareTo(orderPrice) < 0){     //wallet
            throw new BusinessException(SALDO_INSUFICIENTE);
        }

        var balance = actualBalance.subtract(orderPrice); //wallet

        wallet.updateBalance(balance);
        log.info("M=createTransactionBidWallet, newBalance = {}, customerId = {}", balance, wallet.getCustomer().getId());

        walletRepository.save(wallet);
    }

    public void updateTransactionBidWallet(Order order, OrderUpdateRequest orderRequest){
        var originalPrice = order.getOriginalprice(order);

        var wallet = findWalletByCustomerId(order.getCustomer().getId());
        log.info("M=updateTransactionBidWallet, balance = {}, customerId = {}", wallet.getBalance(), wallet.getCustomer().getId());

        var actualBalance = wallet.getBalance();
        var realBalance = actualBalance.add(originalPrice);

        var amount = orderRequest.getAmount();
        var newOrderPrice = orderRequest.getPrice().multiply(BigDecimal.valueOf(amount));

        if(realBalance.compareTo(newOrderPrice) < 0){
            throw new BusinessException(SALDO_INSUFICIENTE);
        }

        var balance = realBalance.subtract(newOrderPrice);
        wallet.updateBalance(balance);
        log.info("M=updateTransactionBidWallet, newBalance = {}, customerId = {}", balance, wallet.getCustomer().getId());

        walletRepository.save(wallet);
    }

    public void cancellTransactionBidWallet(Order order){
        var wallet = findWalletByCustomerId(order.getCustomer().getId());
        log.info("M=cancellTransactionBidWallet, balance = {}, customerId = {}", wallet.getBalance(), wallet.getCustomer().getId());

        var actualBalance = wallet.getBalance();
        var amount = order.getAmount();
        var orderPrice = order.getPrice().multiply(BigDecimal.valueOf(amount));

        var balance = actualBalance.add(orderPrice);
        wallet.updateBalance(balance);
        log.info("M=cancellTransactionBidWallet, newBalance = {}, customerId = {}", balance, wallet.getCustomer().getId());

        walletRepository.save(wallet);
    }

    public void updateSellerWallet(Long sellerId, Order order){
        var sellerWallet = findWalletByCustomerId(sellerId);
        log.info("M=updateSellerWallet, balance = {}, customerId = {}", sellerWallet.getBalance(), sellerWallet.getCustomer().getId());

        var newValue = sellerWallet.getBalance().add(order.getOriginalprice(order));

        sellerWallet.updateBalance(newValue);
        log.info("M=updateSellerWallet, newBalance = {}, customerId = {}", newValue, sellerWallet.getCustomer().getId());

        walletRepository.save(sellerWallet);

    }


}
