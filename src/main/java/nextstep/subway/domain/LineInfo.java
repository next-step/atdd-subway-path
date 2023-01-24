package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineInfo {

    private String name;
    private String color;

    public LineInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    protected LineInfo() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineInfo lineInfo = (LineInfo) o;
        return Objects.equals(name, lineInfo.name) && Objects.equals(color, lineInfo.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
