package orderbook.enuns;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    BIDS,
    ASKS;

    @JsonCreator
    public static OrderType fromString(String value) {
        for (OrderType type : OrderType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("orderType deve ser BIDS ou ASKS");
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
