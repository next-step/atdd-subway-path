package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    private final Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, sections);
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void update(final String name, final String color) {
        final String[] updateFields = validateBeforeUpdate(name, color);
        this.name = updateFields[0];
        this.color = updateFields[1];
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        sections.add(this, upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(final Station station) {
        sections.remove(station);
    }

    private boolean isNull(final String str) {
        return !StringUtils.hasText(str);
    }

    private String[] validateBeforeUpdate(final String name, final String color) {
        final String[] updateFields = new String[2];
        updateFields[0] = name;
        updateFields[1] = color;
        if (isNull(name)) {
            updateFields[0] = this.name;
        }
        if (isNull(color)) {
            updateFields[1] = this.color;
        }
        return updateFields;
    }
}
