package subway.domain;

import java.math.BigDecimal;

public class Kilometer {

    private final BigDecimal amount;

    private Kilometer(BigDecimal amount) {
        this.amount = amount;
    }

    public static Kilometer of(BigDecimal amount) {
        return new Kilometer(amount);
    }

    public static Kilometer of(int amount) {
        return new Kilometer(BigDecimal.valueOf(amount));
    }

    public BigDecimal getValue() {
        return amount;
    }
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public int compareTo(Kilometer kilometer) {
        return amount.compareTo(kilometer.getValue());
    }
}
