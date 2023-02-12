package nextstep.subway.domain.line;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Name {
    private String name;

    public Name(String name) {
        this.name = name;
    }
}
