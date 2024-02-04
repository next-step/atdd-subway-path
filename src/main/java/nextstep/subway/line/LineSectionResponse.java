package nextstep.subway.line;


import nextstep.subway.line.section.SectionResponse;

import java.util.List;

public class LineSectionResponse {
    private Long lineId;
    private String name;
    private List<SectionResponse> sections;

    public LineSectionResponse(Long lineId, String name, List<SectionResponse> sections) {
        this.lineId = lineId;
        this.name = name;
        this.sections = sections;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getName() {
        return name;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }

}
