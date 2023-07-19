package nextstep.subway.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void updateLine(Line line) {
        this.sections.get(0).updateLine(line);
    }

    public void add(Section section) {
        Station upEndStation = getUpEndStation();

        // 역 기준으로 하행 구간을 역으로 찾을 수 있다
        Map<Station, Section> upSectionMap = new HashMap<>();
        for (Section oldSection : sections) {
            upSectionMap.put(oldSection.getUpStation(), oldSection);

            if (oldSection.getUpStation().equals(section.getUpStation()) && oldSection.getDownStation().equals(section.getDownStation())) {
                throw new BusinessException(String.format("이미 등록되어 있는 구간입니다. 상행역ID: %s, 하행역ID: %s", section.getUpStation().getId(), section.getDownStation().getId()));
            }
        }

        List<Station> stations = new ArrayList<>(List.of(sections.get(0).getUpStation()));
        stations.addAll(sections.stream().map(Section::getDownStation).collect(Collectors.toList()));
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new BusinessException(String.format("상행역과 하행역 중 하나는 등록되어 있어야 합니다. 상행역ID: %s, 하행역ID: %s", section.getUpStation().getId(), section.getDownStation().getId()));
        }

        Station currentUpStation = upEndStation;
        while (upSectionMap.containsKey(currentUpStation)) {
            Section currentSection = upSectionMap.get(currentUpStation);
            if (currentSection.getUpStation().equals(section.getUpStation()) || currentSection.getDownStation().equals(section.getDownStation())) {
                currentSection.update(section);
                break;
            }
            currentUpStation = currentSection.getDownStation();
        }

        sections.add(section);
    }

    public void delete(final Station station) {
        if (!getLastSection().getDownStation().equals(station)) {
            throw new BusinessException("하행 종점이 아닌 역을 삭제할 수 없습니다. 역ID: " + station.getId());
        }

        if (sections.size() == 1) {
            throw new BusinessException("상행 종점역과 하행 종점역만 있는 노선의 구간은 삭제할 수 없습니다.");
        }

        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

    public List<Station> getStations() {
        Station upEndStation = getUpEndStation();
        List<Station> stations = new ArrayList<>();
        stations.add(upEndStation);

        // 역 기준으로 하행 구간을 역으로 찾을 수 있다
        Map<Station, Section> upSectionMap = new HashMap<>();
        for (Section oldSection : sections) {
            upSectionMap.put(oldSection.getUpStation(), oldSection);
        }

        Station currentUpStation = upEndStation;
        while (upSectionMap.containsKey(currentUpStation)) {
            Section currentSection = upSectionMap.get(currentUpStation);
            stations.add(currentSection.getDownStation());
            currentUpStation = currentSection.getDownStation();
        }

        return stations;
    }

    public List<Section> getSections() {
        return this.sections;
    }

    private Station getUpEndStation() {
        Set<Station> upStations = new HashSet<>();
        Set<Station> downStations = new HashSet<>();

        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }

        for (Station upStation : upStations) {
            if (!downStations.contains(upStation)) {
                return upStation;
            }
        }

        throw new BusinessException("상행 종점역을 찾을 수 없습니다.");
    }
}