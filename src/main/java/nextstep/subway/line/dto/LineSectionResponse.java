package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;

public class LineSectionResponse {

    private Long id;
    private List<SectionResponse> sections;

    private LineSectionResponse() {
    }

    private LineSectionResponse(Long id, List<SectionResponse> sections) {
        this.id = id;
        this.sections = sections;
    }

    public static LineSectionResponse of(Line line) {
        List<SectionResponse> sectionResponses = SectionResponse.of(line.getSections());
        return new LineSectionResponse(line.getId(), sectionResponses);
    }

    public Long getId() {
        return id;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
