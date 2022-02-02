package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.LineRequest;

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

    /**
     * 기존 구간 상행역과 신규 구간 상행역이 겹치면
     * 1. 기존 구간의 상행역을 신규 구간의 하행역으로 변경한다.
     * 2. 기존 구간의 거리는
     *    기존 구간 거리 - 신규 구간의 절대값으로 변경된다.
     *    신규 구간의 거리는 그대로 저장된다.
     *    ex) 가양역 ~ 등촌역 구간 거리 10m이면, 신규 구간이 가양역 ~ 증미역 거리 3m이면,
     *        가양역 ~ 증미역 구간 거리는 3m 그대로 저장, 증미역 ~ 등촌역 구간 거리는 7m로 변경된다.
     * 세부 로직은 addLineBetweenSection()에서 수행한다.
     * @param 새로운_구간
     */
    public void addSection(Section newSection) {
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
        Station firstUpStation = firstSection.getUpStation();
        Station firstDownStation = firstSection.getDownStation();
        stations.add(firstUpStation);

        // 2. 상행 종점 구간 역 추가
        for (Section section : sections) {
            if (firstDownStation.equals(section.getUpStation())) {
                stations.add(section.getUpStation());
            }
        }

        // 3. 하행 종점역과 구간의 상행역이 같은 역을 하행 종점역으로 추가
        for (int i = 0; i < stations.size(); i++) {
            for (Section section : sections) {
                if (stations.get(stations.size() - 1).equals(section.getUpStation())) {
                    stations.add(section.getDownStation());
                }
            }
        }
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

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
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
