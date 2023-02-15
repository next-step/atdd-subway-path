package nextstep.subway.domain;

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded private Sections sections = new Sections();

    public Line() {}

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void initSection(Section newSection) {
        if (isNegativeDistance(newSection.getDistance())) {
            throw new IllegalArgumentException("구간의 거리는 0보다 커야합니다");
        }

        sections.initSection(newSection);
    }

    public void addSection(Section newSection) {
        sections.addSection(newSection);
    }

    public void deleteSection(Long stationId) {
        sections.deleteSection(stationId);
    }

    public List<Station> getSortedStations() {
        return sections.getSortedStations();
    }

    public Integer getSectionCount() {
        return sections.getSectionSize();
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void updateNameAndColor(@NotBlank String name, @NotBlank String color) {
        if (name.isEmpty()) this.name = name;

        this.color = color;
    }

    private boolean isNegativeDistance(Integer distance) {
        if (distance <= 0) {
            return true;
        }

        return false;
    }
}
