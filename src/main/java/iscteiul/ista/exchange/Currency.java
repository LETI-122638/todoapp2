package iscteiul.ista.exchange;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currency")
public class Currency {

    public static final int CODE_MAX_LENGTH = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "currency_id")
    private Long id;

    // evitar nomes reservados (from/to) na BD
    @Column(name = "from_currency", nullable = false, length = CODE_MAX_LENGTH)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = CODE_MAX_LENGTH)
    private String toCurrency;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "result", nullable = false, precision = 19, scale = 4)
    private BigDecimal result;

    protected Currency() { /* Hibernate */ }

    public Currency(String fromCurrency, String toCurrency, BigDecimal amount, BigDecimal result) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.result = result;
    }

    public Long getId() { return id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getResult() { return result; }
    public void setResult(BigDecimal result) { this.result = result; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) return false;
        if (obj == this) return true;
        Currency other = (Currency) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
