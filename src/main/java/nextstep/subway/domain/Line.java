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
        Map<Station, Station> stationMap = new HashMap<>();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            stationMap.put(upStation, downStation);
        }

        List<Station> extractedStations = new ArrayList<>();
        Station startStation = upStation;
        Station currentStation = startStation;

        while (extractedStations.size() < sections.size() + 1) {
            extractedStations.add(currentStation);
            Station nextStation = stationMap.get(currentStation);
            currentStation = nextStation;
        }

        return extractedStations;
    }


    public void addSection(Section section) {
        boolean isAdd = false;

        // 상행종점역 지정
        if (sections.size() == 0) {
            upStation = section.getUpStation();
            downStation = section.getDownStation();
            isAdd = true;
        }

        // 새로운 구간의 상행역의 기존 구간의 상행역인 경우
        for (Section s : sections) {
            if (s.getUpStation().getName().equals(section.getUpStation().getName())) {
                validateExistUpStation(s, section);

                sections.add(new Section(this, section.getDownStation(), s.getDownStation(),
                        s.getDistance() - section.getDistance()));
                sections.remove(s);
                isAdd = true;
                break;
            }
        }

        // 새로운 구간의 하행역이 노선의 상행역인 경우
        if (upStation.getName().equals(section.getDownStation().getName())) {
            upStation = section.getUpStation();
            isAdd = true;
        }

        if (section.getUpStation().getName().equals(downStation.getName())) {
            isAdd = true;
        }

        // 새로운 구간의 상행역이 노선의 하행역인 경우
        if (isAdd) {
            sections.add(section);
        } else {
            throw new DataIntegrityViolationException("잘못된 지하철 구간 등록입니다.");
        }
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
