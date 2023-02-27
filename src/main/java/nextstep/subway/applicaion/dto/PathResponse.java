package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private List<SectionResponse> sections;

    private Integer distance;

    public PathResponse() {
    }

    public PathResponse(List<SectionResponse> sections, Integer distance) {
        this.sections = sections;
        this.distance = distance;
    }
}
