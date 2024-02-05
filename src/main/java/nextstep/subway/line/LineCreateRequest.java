package nextstep.subway.line;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LineCreateRequest {
    private String name;
    private String color;
    private int distance;
    private Long upstationId;
    private Long downstationId;

    public static Line toEntity(LineCreateRequest request) {
        return Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();
    }
}
