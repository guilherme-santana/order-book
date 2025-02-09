package orderbook.domain.services;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.enuns.OrderType;

import java.math.BigDecimal;

import static orderbook.domain.messages.Messages.*;

public class OrderRequest {
    @NotNull(message = ASSET_ID_DEVE_SER_INFORMADO)
    @Min(value = 1, message = ASSET_INVALIDO)
    private Long assetId;

    @NotNull(message = CUSTOMER_ID_DEVE_SER_INFORMADO)
    @Min(value = 1, message = CUSTOMER_INVALIDO)
    private Long customerId;

    @NotNull(message = ORDER_TYPE_DEVE_SER_INFORMADO)
    private OrderType orderType;

    @NotNull(message = PRICE_DEVE_SER_INFORMADO)
    @DecimalMin(value = "0.01", inclusive = true, message = "price deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = AMOUNT_DEVE_SER_INFORMADO)
    @Min(value = 1, message = "amount deve ser maior que zero")
    private Integer amount;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

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
