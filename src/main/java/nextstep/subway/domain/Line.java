package nextstep.subway.domain;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

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

    public List<Station> getStations() {
        Map<Station, Station> stationMap = buildStationMap();
        List<Station> stations = extractStations(stationMap);

        return stations;
    }

    private List<Station> extractStations(Map<Station, Station> stationMap) {
        List<Station> extractedStations = new ArrayList<>();
        Station currentStation = upStation;
        while (extractedStations.size() < sections.size() + 1) {
            extractedStations.add(currentStation);
            Station nextStation = stationMap.get(currentStation);
            currentStation = nextStation;
        }
        return extractedStations;
    }

    private Map<Station, Station> buildStationMap() {
        Map<Station, Station> stationMap = new HashMap<>();
        sections.stream().forEach(section -> stationMap.put(section.getUpStation(), section.getDownStation()));
        return stationMap;
    }

    public void addSection(Section section) {
        if (section.equalsUpStation(upStation) && section.equalsDownStation(downStation)) {
            throw new DataIntegrityViolationException("상행역과 하행역이 이미 등록된 구간입니다.");
        }

        boolean isAddSection = false;
        if (sections.size() == 0) {
            upStation = section.getUpStation();
            downStation = section.getDownStation();
            isAddSection = true;
        }

        // 새로운 구간의 상행역의 기존 구간의 상행역인 경우
        if(upStationExistUpStation(section)){
            isAddSection = true;
        }

        // 새로운 구간의 하행역이 노선의 상행역인 경우
        if (upStation.equals(section.getDownStation())) {
            upStation = section.getUpStation();
            isAddSection = true;
        }

        // 새로운 구간의 상행역이 노선의 하행역인 경우
        if (downStation.equals(section.getUpStation())) {
            downStation = section.getDownStation();
            isAddSection = true;
        }

        if (!isAddSection) {
            throw new DataIntegrityViolationException("잘못된 지하철 구간 등록입니다.");
        }

        sections.add(section);
    }

    private boolean upStationExistUpStation(Section section) {
        for (Section existSection : sections) {
            if (existSection.equalsUpStation(section.getUpStation())) {
                validateExistUpStation(existSection, section);

                sections.add(new Section(this, section.getDownStation(), existSection.getDownStation()
                        , existSection.getDistance() - section.getDistance()));
                sections.remove(existSection);
                return true;
            }
        }
        return false;
    }

    private void validateExistUpStation(Section existSection, Section section) {
        if (section.isGreaterOrEqualDistance(existSection)) {
            throw new DataIntegrityViolationException("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }

        if (section.equalsUpStationAndDownStation(existSection)) {
            throw new DataIntegrityViolationException("상행역과 하행역이 이미 등록된 구간입니다.");
        }
    }

    public void removeSection() {
        sections.remove(sections.size() - 1);
    }
}
