package nextstep.subway.domain;

import nextstep.subway.ui.exception.AddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
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

    public void addSection(Section newSection) {
        validateStationNotExistInSection(newSection);
        for (Section existingSection : sections) {
            existingSection.addLineBetweenSection(newSection);
        }
        sections.add(newSection);
    }

    public void removeSection(Station downStation) {
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.getDownStation().equals(downStation)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public void updateLine(String name,  String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public List<Station> getStations() {
        return getStations(getFirstSection());
    }

    private List<Station> getStations(Section firstSection) {
        List<Station> stations = new ArrayList<>();
        addEndUpSectionStation(firstSection, stations);
        addEndDownStation(stations);
        return stations;
    }

    // 1. 상행 종점 구간 조회
    private Section getFirstSection() {
        List<Station> downStations = getDownStations();
        for (Section section : sections) {
            int count = 0;
            for (Station downStation : downStations) {
                if (section.getUpStation().equals(downStation)) {
                    count++;
                    break;
                }
            }
            if (count == 0) {
                return section;
            }
        }
        return sections.get(0);
    }

    /**
     *
     * @param firstSection : getFirstSection()에서 가져온 상행 종점 구간이다.
     * @param stations : 빈 객체로 최초로 상행 종점 구간의 역을 추가할 객체이다.
     */
    private void addEndUpSectionStation(Section firstSection, List<Station> stations) {
        stations.add(firstSection.getUpStation());
        stations.add(firstSection.getDownStation());
    }

    /**
     * 상행 종점 구간의 하행 종점역 부터 시작하여 추가되는 하행 종점역과 각 구간의 상행역이 같으면 각 구간의 하행역을 추가한다.
     * @param stations : getFirstSection()에서 가져온 상행 종점 구간의 상행 종점역과 하행 종점역
     */
    private void addEndDownStation(List<Station> stations) {
        for (int i = 0; i < stations.size(); i++) {
            for (Section section : sections) {
                Station endDownStation = stations.get(stations.size() - 1);
                if (endDownStation.equals(section.getUpStation())) {
                    stations.add(section.getDownStation());
                }
            }
        }
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private void validateStationNotExistInSection(Section newSection) {
        if (!getUpStations().contains(newSection.getUpStation()) &&
                !getUpStations().contains(newSection.getDownStation()) &&
                !getDownStations().contains(newSection.getUpStation()) &&
                !getDownStations().contains(newSection.getDownStation()) &&
                !sections.isEmpty()) {
            throw new AddSectionException(
                    String.format("상행역과 하행역 모두 구간에 존재하지 않는 역입니다. 상행역 = %s, 하행역 = %s",
                            newSection.getUpStation().getName(), newSection.getDownStation().getName()));
        }
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
}
