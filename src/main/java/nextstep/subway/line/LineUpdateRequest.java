package nextstep.subway.line;

import java.util.Objects;

public class LineUpdateRequest {
    private String name;
    private String color;

    public LineUpdateRequest() {}

    public LineUpdateRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineUpdateRequest that = (LineUpdateRequest) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(
            getColor(), that.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getColor());
    }
}
