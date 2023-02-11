package nextstep.subway.applicaion.dto;

import java.util.List;

public class LineSectionResponse {

    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections;

    public LineSectionResponse(Long id, String name, String color, List<SectionResponse> sections) {
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

    public List<SectionResponse> getSections() {
        return sections;
    }

}
