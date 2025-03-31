package orderbook.domain.services;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.domain.messages.Messages;
import orderbook.enuns.OrderType;

import java.math.BigDecimal;


public class WalletRequest {

    @NotNull(message = Messages.CUSTOMER_ID_DEVE_SER_INFORMADO)
    @Min(value = 1, message = Messages.CUSTOMER_INVALIDO)
    private Long customerId;

    @NotNull(message = Messages.VALOR_INVALIDO)
    @DecimalMin(value = "0.01", inclusive = true, message = "price deve ser maior que zero")
    @Column(columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal balance;


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
