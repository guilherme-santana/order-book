package orderbook.domain.services;

import orderbook.dataprovider.repositories.WalletRepository;
import orderbook.domain.models.Order;
import orderbook.domain.models.Wallet;
import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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


    public void createTransactionBidWallet(Order order){
        Wallet wallet = findWalletByCustomerId(order.getCustomer().getId());
        BigDecimal actualBalance = wallet.getBalance();
        Integer amount = order.getAmount();
        BigDecimal orderPrice = order.getPrice().multiply(BigDecimal.valueOf(amount));

        if(actualBalance.compareTo(orderPrice) < 0){
            throw new ExceptionOrder("Saldo insuficiente!");
        }

        BigDecimal balance = actualBalance.subtract(orderPrice);
        wallet.updateBalance(balance);

        walletRepository.save(wallet);
    }

    public void updateTransactionBidWallet(Order order, OrderRequest orderRequest){
        BigDecimal originalPrice = order.getOriginalprice(order);

        Wallet wallet = findWalletByCustomerId(order.getCustomer().getId());
        BigDecimal actualBalance = wallet.getBalance();
        BigDecimal realBalance = actualBalance.add(originalPrice);

        Integer amount = orderRequest.getAmount();
        BigDecimal newOrderPrice = orderRequest.getPrice().multiply(BigDecimal.valueOf(amount));

        if(realBalance.compareTo(newOrderPrice) < 0){
            throw new ExceptionOrder("Saldo insuficiente!");
        }

        BigDecimal balance = realBalance.subtract(newOrderPrice);
        wallet.updateBalance(balance);

        walletRepository.save(wallet);
    }

    public void cancellTransactionBidWallet(Order order){
        Wallet wallet = findWalletByCustomerId(order.getCustomer().getId());
        BigDecimal actualBalance = wallet.getBalance();
        Integer amount = order.getAmount();
        BigDecimal orderPrice = order.getPrice().multiply(BigDecimal.valueOf(amount));

        BigDecimal balance = actualBalance.add(orderPrice);
        wallet.updateBalance(balance);

        walletRepository.save(wallet);
    }

    public void updateSellerWallet(Long sellerId, Order order){
        Wallet sellerWallet = findWalletByCustomerId(sellerId);

        BigDecimal newValue = sellerWallet.getBalance().add(order.getOriginalprice(order));

        sellerWallet.updateBalance(newValue);

        walletRepository.save(sellerWallet);

    }


}
