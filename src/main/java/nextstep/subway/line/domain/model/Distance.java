package nextstep.subway.line.domain.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import nextstep.subway.common.domain.model.exception.ErrorMessage;

@Getter
@Embeddable
public class Distance {
    @JsonValue
    @Column(name = "DISTANCE", nullable = false)
    private int value;

    protected Distance() {
    }

    public Distance(int value) {
        verifyZero(value);
        this.value = value;
    }

    private void verifyZero(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                ErrorMessage.INVALID_DISTANCE.getMessage()
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance)o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
