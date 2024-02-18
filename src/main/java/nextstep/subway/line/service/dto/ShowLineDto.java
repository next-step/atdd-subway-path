package nextstep.subway.line.service.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.service.dto.ShowLineSectionDto;

import java.util.List;
import java.util.stream.Collectors;

public class ShowLineDto {

    private Long lineId;

    private String name;

    private String color;

    private List<ShowLineSectionDto> sections;

    private ShowLineDto() {
    }

    private ShowLineDto(Long lineId, String name, String color, List<ShowLineSectionDto> sections) {
        this.lineId = lineId;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static ShowLineDto from(Line line) {
        return new ShowLineDto(
                line.getLineId(),
                line.getName(),
                line.getColor(),
                line.getSections().stream()
                        .map(ShowLineSectionDto::from)
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

    public List<ShowLineSectionDto> getSections() {
        return sections;
    }

}
