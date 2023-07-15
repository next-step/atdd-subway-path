package subway.line.model;

import lombok.NoArgsConstructor;
import subway.exception.SubwayBadRequestException;
import subway.exception.SubwayNotFoundException;
import subway.line.constant.SubwayMessage;
import subway.station.model.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class LineSections {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section newSection, Line line) {

        if (hasSections(line)) {
            appendSection(newSection);
        }

        newSection.setLine(line);
        addSection(newSection);
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
        validRemoveStationIsDownStationInExistLine(targetStation);

        Section lastSection = getLastSection();
        this.remove(lastSection);
    }

    private void remove(Section section) {
        sections.remove(section);
    }

    private void addSection(Section newSection) {
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

    private Optional<Section> findSectionWithUpStationByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny();
    }

    private boolean hasSections(Line line) {
        return line.getLineSections().sections.size() > 0;
    }

    private void appendSection(Section newSection) {
        List<Station> stations = getStations();

        validStationInNewSectionIsNotDuplicatedStationInExistLine(newSection, stations);
        validStationInNewSectionIsStationInExistLine(newSection, stations);

        appendMiddle(newSection);
    }

    private void appendMiddle(Section newSection) {
        appendMiddleDown(newSection);
        appendMiddleUp(newSection);
    }

    private void appendMiddleDown(Section newSection) {
        Optional<Section> sectionWithDownStationByDownStation = findSectionWithDownStationByDownStation(newSection.getDownStation());
        Optional<Section> sectionWithDownStationByUpStation = findSectionWithDownStationByUpStation(newSection.getUpStation());

        if (sectionWithDownStationByDownStation.isPresent() && sectionWithDownStationByUpStation.isEmpty()) {
            Section existSection = sectionWithDownStationByDownStation.get();
            existSection.changeDownStation(newSection);
        }

    }

    private void appendMiddleUp(Section newSection) {
        Optional<Section> sectionWithUpStationByUpStation = findSectionWithUpStationByUpStation(newSection.getUpStation());
        Optional<Section> sectionWithUpStationByDownStation = findSectionWithUpStationByDownStation(newSection.getDownStation());

        if (sectionWithUpStationByUpStation.isPresent() && sectionWithUpStationByDownStation.isEmpty()) {
            Section existSection = sectionWithUpStationByUpStation.get();
            existSection.changeUpStation(newSection);
        }

    }

    private Optional<Section> findSectionWithDownStationByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findAny();
    }

    private Optional<Section> findSectionWithUpStationByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findAny();
    }

    private Optional<Section> findSectionWithDownStationByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
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
            throw new SubwayBadRequestException(SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE_MESSAGE.getCode(),
                    SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE_MESSAGE.getFormatMessage(section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

    private void validStationsCountIsOverMinimalSectionSize() {
        if (this.getSectionsCount() < MINIMAL_SECTION_SIZE) {
            throw new SubwayBadRequestException(SubwayMessage.STATION_DELETE_MINIMAL_VALID_MESSAGE.getCode(),
                    SubwayMessage.STATION_DELETE_MINIMAL_VALID_MESSAGE.getFormatMessage(MINIMAL_SECTION_SIZE));
        }
    }

    private void validRemoveStationIsDownStationInExistLine(Station targetStation) {
        Section lastSection = getLastSection();
        Station downStation = lastSection.getDownStation();
        if (!downStation.equals(targetStation)) {
            throw new SubwayBadRequestException(SubwayMessage.SECTION_DELETE_LAST_STATION_VALID_MESSAGE);
        }
    }
}
