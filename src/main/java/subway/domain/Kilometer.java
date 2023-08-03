package subway.domain;

import java.math.BigDecimal;
import java.util.Objects;

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

    public Kilometer minus(Kilometer distance) {
        return Kilometer.of(amount.subtract(distance.amount));
    }

    public void validate() {
        if (!isPositive()) {
            throw new IllegalArgumentException("거리는 0 초과이어야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kilometer kilometer = (Kilometer) o;
        return Objects.equals(amount.doubleValue(), kilometer.amount.doubleValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
