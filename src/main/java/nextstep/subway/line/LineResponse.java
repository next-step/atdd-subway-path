package nextstep.subway.line;

import nextstep.subway.line.section.Section;

import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<Section> sections;


    public static LineResponse of(Line line){
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections());
    }

    private LineResponse(Long id, String name, String color, List<Section> sections) {
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

    public List<Section> getSections() {
        return sections;
    }
}
