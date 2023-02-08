package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(this, section.getDownStation(), oldSection.getDownStation(), section.getDistance()));
                    sections.remove(oldSection);
                });

        sections.stream()
                .filter(oldSection -> oldSection.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(this, oldSection.getUpStation(), section.getUpStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });

        sections.add(section);
        section.setLine(this);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());

        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findNextOf(nextSection);
        }

        return stations;
    }

    private Section findUpEndSection() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow();
    }

    private Section findNextOf(Section section) {
        return sections.stream()
                .filter(it -> section.getDownStation() == it.getUpStation())
                .findFirst()
                .orElse(null);
    }

    public void removeSection(long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 1개인 경우 삭제할 수 없습니다.");
        }

        Section lastSection = sections.get(sections.size() - 1);;
        if (!lastSection.isDownStationId(stationId)) {
            throw new IllegalArgumentException("마지막 구간만 제거할 수 있습니다.");
        }

        sections.remove(lastSection);
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }
}
