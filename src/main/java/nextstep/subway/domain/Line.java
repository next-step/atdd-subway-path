package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty() || isLastStation(upStation) || isFirstStation(downStation)) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }

        Section ordinarySection = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("구간 추가 불가"));
        sections.remove(ordinarySection);
        sections.add(new Section(this, downStation, ordinarySection.getDownStation(), ordinarySection.getDistance() - distance));
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private boolean isLastStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .filter(it -> !it.getUpStation().equals(station))
                .findFirst().isPresent();
    }

    private boolean isFirstStation(Station station) {
        return sections.stream().map(it -> it.getUpStation()).anyMatch(it -> it.equals(station))
                && sections.stream().map(it -> it.getDownStation()).noneMatch(it -> it.equals(station));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> result = new ArrayList<>();
        result.add(sections.stream()
                .map(it -> it.getUpStation())
                .filter(it -> isFirstStation(it))
                .findFirst().get());

        while (result.size() != sections.size() + 1) {
            Station station = sections.stream()
                    .filter(it -> it.getUpStation().equals(result.get(result.size() - 1)))
                    .findFirst().get().getDownStation();
            result.add(station);
        }

        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void removeSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }
}
