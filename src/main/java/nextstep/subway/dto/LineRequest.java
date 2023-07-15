package nextstep.subway.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineRequest {

    private String name;
    private String color;


    public Line toEntity() {
        return new Line(
                this.name,
                this.color
        );
    }

}
