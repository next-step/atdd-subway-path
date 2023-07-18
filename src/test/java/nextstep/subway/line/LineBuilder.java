package nextstep.subway.line;

import static common.Constants.신분당선;

import nextstep.subway.section.Sections;

public class LineBuilder {

    private Long id = 1L;
    private String name = 신분당선;
    private String color = "bg-red-600";
    private Sections sections;
    private int distance = 10;

    private LineBuilder() {}

    public static LineBuilder aLine() {
        return new LineBuilder();
    }

    public LineBuilder withId(Long id) {
        this.id = id;

        return this;
    }

    public LineBuilder withName(String name) {
        this.name = name;

        return this;
    }

    public LineBuilder withColor(String color) {
        this.color = color;

        return this;
    }

    public LineBuilder withSections(Sections sections) {
        this.sections = sections;

        return this;
    }

    public Line build() {
        return new Line(id, name, color, sections);
    }

}
