package subway.line.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.exception.SubwayBadRequestException;
import subway.exception.SubwayNotFoundException;
import subway.constant.SubwayMessage;
import subway.station.model.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Getter
@Embeddable
@NoArgsConstructor
public class LineSections {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section newSection, Line line) {

        if (hasSections(line)) {
            putInSections(newSection);
        }

        newSection.setLine(line);
        add(newSection);
    }

    public Station getFirstStation() {
        List<Station> stations = getStations();
        return stations.get(0);
    }

    public Station getLastStation() {
        List<Station> stations = getStations();
        return stations.get(stations.size() - 1);
    }

    public List<Station> getStations() {
        Map<Station, Station> stationMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        Station upStation = sections.stream()
                .map(Section::getUpStation)
                .filter(station -> !stationMap.containsValue(station))
                .findFirst()
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_BY_UP_STATION));

        List<Station> stations = new ArrayList<>();
        Station nextStation = upStation;

        while (nextStation != null) {
            stations.add(nextStation);
            nextStation = stationMap.get(nextStation);
        }

        return stations;
    }

    public long getSectionsCount() {
        return this.sections.size();
    }

    public void removeSectionByStation(Station targetStation) {
        validStationsCountIsOverMinimalSectionSize();

        List<Station> stations = getStations();
        int findIndex = findStationIndex(targetStation, stations);

        if (isMiddleStation(stations, findIndex)) {
            removeMiddleStation(targetStation);
        }
        if (isFirstStation(findIndex)) {
            removeStationInSectionWithUpStation(targetStation);
        }
        if (isLastStation(stations, findIndex)) {
            removeStationInSectionWithDownStation(targetStation);
        }
    }

    private int findStationIndex(Station targetStation, List<Station> stations) {
        return IntStream.range(0, stations.size())
                .filter(idx -> targetStation.equals(stations.get(idx)))
                .findFirst()
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.STATION_NOT_FOUND_IN_SECTION));
    }

    private boolean isMiddleStation(List<Station> stations, int findIndex) {
        return findIndex != 0 && findIndex != stations.size() - 1;
    }

    private boolean isFirstStation(int findIndex) {
        return findIndex == 0;
    }

    private boolean isLastStation(List<Station> stations, int findIndex) {
        return findIndex == stations.size() - 1;
    }

    private void removeMiddleStation(Station targetStation) {
        Section upSection = findSectionWithDownStationByStation(targetStation)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_DOWN_STATION_IN_SECTION));
        Section downSection = findSectionWithUpStationByStation(targetStation)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_UP_STATION_IN_SECTION));

        upSection.pullDownStationFromUpStationOfTargetSection(downSection);
        remove(downSection);
    }

    private void removeStationInSectionWithUpStation(Station targetStation) {
        Section section = findSectionWithUpStationByStation(targetStation)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_UP_STATION_IN_SECTION));
        remove(section);
    }

    private void removeStationInSectionWithDownStation(Station targetStation) {
        Section section = findSectionWithDownStationByStation(targetStation)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_DOWN_STATION_IN_SECTION));
        remove(section);
    }

    private void remove(Section section) {
        this.sections.remove(section);

    }

    private void add(Section newSection) {
        this.sections.add(newSection);
    }

    private long getTotalDistance() {
        return sections.stream()
                .map(Section::getDistance)
                .reduce(0L, Long::sum);
    }

    private Section getLastSection() {
        int lastSectionIndex = this.sections.size() - 1;
        return this.sections.get(lastSectionIndex);
    }

    private boolean hasSections(Line line) {
        return line.getLineSections().sections.size() > 0;
    }

    private void putInSections(Section newSection) {
        List<Station> stations = getStations();

        validStationInNewSectionIsNotDuplicatedStationInExistLine(newSection, stations);
        validStationInNewSectionIsStationInExistLine(newSection, stations);

        putInMiddle(newSection);
    }

    private void putInMiddle(Section newSection) {
        putInBackSectionOfMiddle(newSection);
        putInFrontSectionOfMiddle(newSection);
    }

    private void putInBackSectionOfMiddle(Section newSection) {
        Optional<Section> sectionByUpStation = findSectionWithDownStationByStation(newSection.getUpStation());
        Optional<Section> sectionByDownStation = findSectionWithDownStationByStation(newSection.getDownStation());

        if (sectionByDownStation.isPresent() && sectionByUpStation.isEmpty()) {
            Section existSection = sectionByDownStation.get();
            existSection.changeDownStation(newSection);
        }
    }

    private void putInFrontSectionOfMiddle(Section newSection) {
        Optional<Section> sectionByUpStation = findSectionWithUpStationByStation(newSection.getUpStation());
        Optional<Section> sectionByDownStation = findSectionWithUpStationByStation(newSection.getDownStation());

        if (sectionByUpStation.isPresent() && sectionByDownStation.isEmpty()) {
            Section existSection = sectionByUpStation.get();
            existSection.changeUpStation(newSection);
        }
    }

    private Optional<Section> findSectionWithUpStationByStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny();
    }

    private Optional<Section> findSectionWithDownStationByStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findAny();
    }

    private Optional<Station> findAnyStationInNewSectionIsStationInExistLine(Section section, List<Station> stationsInLine) {
        return stationsInLine.stream()
                .filter(station -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .findAny();
    }


    private void validStationInNewSectionIsStationInExistLine(Section section, List<Station> stationsInLine) {
        Optional<Station> findStation = findAnyStationInNewSectionIsStationInExistLine(section, stationsInLine);
        if (findStation.isEmpty()) {
            throw new SubwayBadRequestException(SubwayMessage.SECTION_ADD_STATION_NOT_FOUND_ANYONE_MESSAGE.getCode(),
                    SubwayMessage.SECTION_ADD_STATION_NOT_FOUND_ANYONE_MESSAGE.getFormatMessage(section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

    private void validStationInNewSectionIsNotDuplicatedStationInExistLine(Section section, List<Station> stationsInLine) {
        Optional<Station> findUpStation = stationsInLine.stream()
                .filter(station -> station.equals(section.getUpStation()))
                .findAny();
        Optional<Station> findDownStation = stationsInLine.stream()
                .filter(station -> station.equals(section.getDownStation()))
                .findAny();
        if (findUpStation.isPresent() && findDownStation.isPresent()) {
            throw new SubwayBadRequestException(SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE.getCode(),
                    SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE.getFormatMessage(section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

    private void validStationsCountIsOverMinimalSectionSize() {
        if (this.getSectionsCount() < MINIMAL_SECTION_SIZE) {
            throw new SubwayBadRequestException(SubwayMessage.STATION_DELETE_MINIMAL_VALID.getCode(),
                    SubwayMessage.STATION_DELETE_MINIMAL_VALID.getFormatMessage(MINIMAL_SECTION_SIZE));
        }
    }
}
