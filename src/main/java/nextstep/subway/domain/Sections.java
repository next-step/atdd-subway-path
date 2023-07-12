package nextstep.subway.domain;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class Sections {
    public Sections() {

    }

    public Sections(Line line) {
        this.line = line;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Station downStation;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

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
        if (upStationExistUpStation(section)) {
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

                sections.add(new Section(line, section.getDownStation(), existSection.getDownStation()
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

    public void removeLastStation() {
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        Map<Station, Station> stationMap = buildStationMap();
        List<Station> stations = extractStations(stationMap);

        return stations;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int size() {
        return sections.size();
    }

    public Station getDownStation() {
        return downStation;
    }
}
