package nextstep.subway.domain;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.*;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name="section_order")
    private List<Section> sections = new LinkedList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void addSection(Section section) {
        List<Station> stations = getStations();
        validateAddSection(stations, section);

        if (stations.isEmpty()) {
            sections.add(section);
            return;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section origin = sections.get(i);
            if (Objects.equals(origin.getUpStation(), section.getUpStation())) {
                addSectionBackOfUpStation(origin, section, i);
            } else if (Objects.equals(origin.getDownStation(), section.getDownStation())) {
                addSectionFrontOfDownStation(origin, section, i);
            } else if (Objects.equals(origin.getUpStation(), section.getDownStation())) {
                addUpStation(section);
            } else if (Objects.equals(origin.getDownStation(), section.getUpStation())) {
                addDownStation(section);
            } else {
                continue;
            }
            updateOrderValues();
            return;
        }
    }

    private void addUpStation(Section section) {
        sections.add(0, section);
    }

    private void addDownStation(Section section) {
        sections.add(section);
    }

    private void addSectionBackOfUpStation(Section origin ,Section newbie, int i) {
        validateDistance(newbie, origin);

        Section right = new Section(this, newbie.getDownStation(), origin.getDownStation(), origin.getDistance() - newbie.getDistance());
        sections.set(i, newbie);
        sections.add(i + 1, right);
    }

    private void addSectionFrontOfDownStation(Section origin, Section newbie, int i) {
        validateDistance(newbie, origin);
        Section left = new Section(this, origin.getUpStation(), newbie.getUpStation(), origin.getDistance() - newbie.getDistance());
        sections.set(i, left);
        sections.add(i + 1, newbie);
    }


    private void validateAddSection(List<Station> stations, Section section) {
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new DataIntegrityViolationException("둘 다 등록된 역입니다.");
        }

        if (!stations.isEmpty() && !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new DataIntegrityViolationException("노선에 추가하려는 역이 모두 등록되지 않은 역입니다");
        }
    }

    private void validateDistance(Section newbie, Section origin) {
        if (newbie.getDistance() >= origin.getDistance()) {
            throw new DataIntegrityViolationException("노선에 추가하려는 중간 구간의 거리가 기존 구간보다 깁니다");
        }
    }

    public void removeSections(Section section) {
        sections.remove(section);
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        if (stations.isEmpty()) {
            return stations;
        }
        stations.add(sections.get(sections.size() - 1).getDownStation());
        return stations;
    }

    private void updateOrderValues() {
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setOrder(i);
        }
    }
}
