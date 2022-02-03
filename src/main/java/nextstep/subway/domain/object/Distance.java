package nextstep.subway.domain.object;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
@NoArgsConstructor
public class Distance {
    @Column
    @Min(0)
    @NotNull
    private Integer value;

    public Distance(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
