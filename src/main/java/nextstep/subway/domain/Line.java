package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public void updateLine(final String name, final String color) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);

        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        sections.addSection(section);
    }

    public void removeSection(final Station station) {
        sections.removeSection(station);
    }

    public List<Station> getStations() {
        if (isNewLine()) {
            return Collections.emptyList();
        }

        return sections.getStations();
    }

    private boolean isNewLine() {
        return sections.isEmpty();
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
