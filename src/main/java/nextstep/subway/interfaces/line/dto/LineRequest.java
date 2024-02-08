package nextstep.subway.interfaces.line.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
public class LineRequest {
    @Getter
    @NoArgsConstructor
    public static class Line {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private Long distance;
    }


    @Getter
    @NoArgsConstructor
    public static class Section {
        private Long upStationId;
        private Long downStationId;
        private Long distance;
    }
}
