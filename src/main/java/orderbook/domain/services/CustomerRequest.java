package orderbook.domain.services;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import orderbook.domain.messages.Messages;
import orderbook.domain.models.Customer;

import java.math.BigDecimal;


public class CustomerRequest {

    @NotNull(message = Messages.VALOR_INVALIDO)
    private String name;

    @NotNull(message = Messages.VALOR_INVALIDO)
    private Integer document;

    @NotNull(message = Messages.VALOR_INVALIDO)
    @Email(message = Messages.EMAIL_INVALIDO)
    private String email;

    @NotNull(message = Messages.VALOR_INVALIDO)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDocument() {
        return document;
    }

    public void setDocument(Integer document) {
        this.document = document;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
