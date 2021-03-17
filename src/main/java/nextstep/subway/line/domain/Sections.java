package nextstep.subway.line.domain;

import nextstep.subway.exception.DuplicateStationException;
import nextstep.subway.exception.NotEqualsLastStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    private int size() {
        return sections.size();
    }

    private boolean isEqualsLastStation(Section section, Station station) {
        String newUpStationName = section.getUpStation().getName();
        String currentLastSationName = station.getName();
        return newUpStationName.equals(currentLastSationName);
    }

    private boolean isExistedStation(List<Station> stations, Section section) {
        return stations.stream().anyMatch(i -> i.getId().equals(section.getDownStation().getId()));
    }

    private void addSectionValidate(List<Station> allStations, Section newSection) {
        Station lastStation = getLastSection().getDownStation();

        if (!isEqualsLastStation(newSection, lastStation)) {
            throw new NotEqualsLastStationException();
        }

        if (isExistedStation(allStations, newSection)) {
            throw new DuplicateStationException();
        }
    }

    private void isValidDeleteSection(Long stationId) {
        if (getLastSection().getDownStation().getId() != stationId) {
            throw new IllegalArgumentException("종점역만 삭제가 가능합니다.");
        }
        if (size() == 1)
            throw new RuntimeException("마지막 구간은 삭제할 수 없습니다.");
    }

    public Section getLastSection() {
        return sections.get(size() - 1);
    }

    public void addSection(Section newSection) {
        List<Station> stations = getAllStations();
        if (stations.size() == 0) {
            sections.add(newSection);
            return;
        }

        addSectionValidate(stations, newSection);
        sections.add(newSection);
    }

    public void deleteLastSection(Long stationId) {
        isValidDeleteSection(stationId);
        sections.remove(getLastSection());
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> responses = new ArrayList<>();
        responses.add(sections.get(0).getUpStation());

        sections.stream().map(section -> section.getDownStation()).forEach(responses::add);
        return responses;
    }

    public List<Section> getAllSections(){
        return sections;
    }

}