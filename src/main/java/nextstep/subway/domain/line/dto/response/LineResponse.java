package nextstep.subway.domain.line.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.domain.Section;
import nextstep.subway.domain.station.dto.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<SectionResponse> sections;

    @Builder
    private LineResponse(Long id, String name, String color, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static LineResponse from(Line line) {
        List<Section> lineSections = line.getSections();

        List<SectionResponse> sectionResponses = lineSections.stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .sections(sectionResponses)
                .build();
    }
}
