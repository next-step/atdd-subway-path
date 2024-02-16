package nextstep.subway.line.presentation.response;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.service.SectionDto;

import java.util.List;
import java.util.stream.Collectors;

public class ShowLineResponse {

    private Long lineId;

    private String name;

    private String color;

    private List<SectionDto> sections;

    private ShowLineResponse() {
    }

    public ShowLineResponse(Long lineId, String name, String color, List<SectionDto> sections) {
        this.lineId = lineId;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static ShowLineResponse from(Line line) {
        return new ShowLineResponse(
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
