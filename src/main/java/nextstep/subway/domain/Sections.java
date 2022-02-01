package nextstep.subway.domain;

import static nextstep.subway.exception.CommonExceptionMessages.ALREADY_HAS_STATIONS;
import static nextstep.subway.exception.CommonExceptionMessages.NOT_HAS_ANY_STATIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.domain.utils.StationConnector;
import nextstep.subway.domain.utils.StationFrontConnector;
import nextstep.subway.domain.utils.StationRearConnector;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

         sectionAdd(newSection);
    }

    private void sectionAdd(Section newSection) {
        validateAddable(newSection);

        for (Section section : sections) {
            handleMidCase(section, newSection);
        }

        sections.add(newSection);
    }

    private void handleMidCase(Section section, Section newSection) {
        if (section.hasSameUpStationWith(newSection)) {
            section.addUpMid(newSection);
        }

        if (section.hasSameDownStationWith(newSection)) {
            section.addMidDown(newSection);
        }
    }

    private void validateAddable(Section newSection) {
        if (hasAllStationsOf(newSection)) {
            throw new DataIntegrityViolationException(ALREADY_HAS_STATIONS);
        }

        if (!existAnyStationsOf(newSection)) {
            throw new DataIntegrityViolationException(NOT_HAS_ANY_STATIONS);
        }
    }

    public boolean hasAllStationsOf(Section section) {
        return containsStation(section.getUpStation())
            && containsStation(section.getDownStation());
    }

    public boolean existAnyStationsOf(Section section) {
        return containsStation(section.getUpStation())
            || containsStation(section.getDownStation());
    }

    private boolean containsStation(Station station) {
        return sections.stream()
            .anyMatch(section -> section.hasStation(station));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int size() {
        return sections.size();
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public void remove(int index) {
        sections.remove(index);
    }

    public int getTotalDistance() {
        return sections.stream()
            .mapToInt(Section::getDistance)
            .sum();

    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        makeFrontSideOf(stations);
        makeRearSideOf(stations);

        return stations;
    }

    private void makeFrontSideOf(List<Station> stations) {
        Station upStation = sections.get(0).getUpStation();
        stations.add(upStation);

        StationFrontConnector connector = new StationFrontConnector(
            sections, stations, upStation
        );

        while (connector.hasNextStation()) {
            stations.add(0, connector.nextStation());
        }

    }

    private void makeRearSideOf(List<Station> stations) {
        Station downStation = sections.get(0).getDownStation();
        stations.add(downStation);

        StationConnector connector = new StationRearConnector(
            sections, stations, downStation
        );

        while (connector.hasNextStation()) {
            stations.add(connector.nextStation());
        }
    }

    public boolean isLessThanTwo() {
        return sections.size() < 2;
    }

    public void removeSectionBy(Station station) {
        List<Section> sectionListIncluding = findSectionListIncluding(station);

        // 최상행
        // // 구간 1개에서 -> 상행 1개
        // 중간
        // // 구간 2개에서 -> 하행1, 상행1
        // 최하행
        // // 구간 1개에서 -> 하행 1개

        if (sectionListIncluding.size() == 0) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역은 삭제 할 수 없음.");
        }

        if (sectionListIncluding.size() == 1) {
            if (sectionListIncluding.get(0).getUpStation().equals(station)) {
                // 최상행
                sections.remove(sectionListIncluding.get(0));
                return;
            }

            if (sectionListIncluding.get(0).getDownStation().equals(station)) {
                // 최하행
                sections.remove(sectionListIncluding.get(0));
                return;
            }
        }

        if (sectionListIncluding.size() == 2) {
            // 중간
            Section section1 = sectionListIncluding.get(0);
            Section section2 = sectionListIncluding.get(1);

            if (section1.getDownStation().equals(station)) {
                section1.setDownStation(section2.getDownStation());
                section1.setDistance(section1.getDistance() + section2.getDistance());
                sections.remove(section2);
                return;
            }

            if (section1.getUpStation().equals(station)) {
                section1.setUpStation(section2.getUpStation());
                section1.setDistance(section1.getDistance() + section2.getDistance());
                sections.remove(section2);
                return;
            }
        }
    }

    private List<Section> findSectionListIncluding(Station station) {
        List<Section> sectionList = new ArrayList<>();

        for (Section section : sections) {
            if (section.getUpStation().equals(station)) {
                sectionList.add(section);
            }

            if (section.getDownStation().equals(station)) {
                sectionList.add(section);
            }
        }

        return sectionList;
    }
}
