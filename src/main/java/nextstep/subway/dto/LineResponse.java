package nextstep.subway.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private List<SectionResponse> sections;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(),
                                line.getName(),
                                line.getColor(),
                                StationResponse.createStationsResponse(line.getSections()),
                                SectionResponse.createSectionResponse(line.getSections()));
    }
}
