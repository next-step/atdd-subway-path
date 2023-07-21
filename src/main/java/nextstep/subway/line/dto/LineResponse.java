package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<SectionOnLineResponse> sections;

    public static LineResponse of(Line line) {
        List<SectionOnLineResponse> sectionOnLineResponses = line.getSections().stream()
                .map(SectionOnLineResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                sectionOnLineResponses
        );
    }

    public LineResponse(Long id, String name, String color, List<SectionOnLineResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<SectionOnLineResponse> getSections() {
        return sections;
    }
}
