package nextstep.subway.domain.line.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.line.domain.Line;

@Getter
public class LineResponse {

    private Long id;

    private String name;

    private String color;

    @Builder
    private LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .build();
    }
}
