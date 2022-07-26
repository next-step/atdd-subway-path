package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public Section getLastSection() {
        return getSections().get(sections.getSize()-1);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
    }

    public void validateRemoveSection(Station station) {
        if (!getLastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException("마지막 구간만 삭제할 수 있습니다.");
        }
    }
}
