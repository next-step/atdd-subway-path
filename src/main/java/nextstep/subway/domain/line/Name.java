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
        if (name == null) {
            throw new IllegalArgumentException("이름은 필수요소 입니다.");
        }
        this.name = name;
    }
}
