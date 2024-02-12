package nextstep.subway.line.presentation.response;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.service.SectionDto;
import nextstep.subway.station.service.StationDto;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateLineResponse {

    private Long lineId;

    private String name;

    private String color;

    private List<SectionDto> sections;

    private UpdateLineResponse() {
    }

    public UpdateLineResponse(Long lineId, String name, String color, List<SectionDto> sections) {
        this.lineId = lineId;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static UpdateLineResponse from(Line line) {
        return new UpdateLineResponse(
                line.getLineId(),
                line.getName(),
                line.getColor(),
                line.getSections().stream()
                        .map(SectionDto::from)
                        .collect(Collectors.toList())
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<SectionDto> getSections() {
        return sections;
    }

}
