package orderbook.domain.services;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import orderbook.dataprovider.exceptions.BusinessException;

import java.math.BigDecimal;

import static orderbook.dataprovider.exceptions.Messages.*;

public class OrderUpdateRequest {

    @NotNull(message = PRICE_DEVE_SER_INFORMADO)
    @DecimalMin(value = "0.01", inclusive = true, message = "price deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = AMOUNT_DEVE_SER_INFORMADO)
    @Min(value = 1, message = "amount deve ser maior que zero")
    private Integer amount;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PRICE_NAO_PODE_SER_MENOR_IGUAL_A_ZERO);
        }
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        if (amount <= 0) {
            throw new BusinessException(AMOUNT_NAO_PODE_SER_MENOR_IGUAL_A_ZERO);
        }
        this.amount = amount;
    }

}
