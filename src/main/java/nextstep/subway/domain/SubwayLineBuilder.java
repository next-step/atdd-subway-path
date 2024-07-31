package nextstep.subway.domain;

public class SubwayLineBuilder {
    private String name;
    private String color;
    private Section section;

    public SubwayLineBuilder() {
    }

    public SubwayLineBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SubwayLineBuilder color(String color) {
        this.color = color;
        return this;

    }

    public SubwayLineBuilder section(Section section) {
        this.section = section;
        return this;
    }

    public SubwayLine build() {
        if (this.name == null || this.color == null || this.section == null){
            throw new IllegalArgumentException("필수값 입니다");
        }
        var subwayLine = new SubwayLine(name, color);
        subwayLine.addInitSection(section);
        return subwayLine;
    }
}
