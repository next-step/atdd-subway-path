package nextstep.subway.domain;

import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public boolean isContain(final Section section) {
        Set<Section> containCheck = new HashSet<>(sections);
        return containCheck.contains(section);
    }

    public void removeSection(final Long stationId) {
        checkIsDownEndStation(stationId);
        sections.remove(size()-1);
    }

    private void checkIsDownEndStation(final Long stationId) {
        if (!getDownEndStation().getId().equals(stationId)) {
            throw new IllegalArgumentException();
        }
    }

    public void update(final String name, final String color) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(color)) {
            this.color = color;
        }
    }

    public int size() {
        return sections.size();
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                                         .map(Section::getUpStation)
                                         .collect(Collectors.toList());
        stations.addAll(sections.stream()
                                .map(Section::getDownStation)
                                .collect(Collectors.toList()));
        return stations;
    }

    public Station getDownEndStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }
}
