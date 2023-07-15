package subway.line.model;

import lombok.NoArgsConstructor;
import subway.exception.SubwayBadRequestException;
import subway.exception.SubwayNotFoundException;
import subway.line.constant.SubwayMessage;
import subway.line.dto.SectionAppendResponse;
import subway.station.model.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@NoArgsConstructor
public class LineSections {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public SectionAppendResponse add(Section newSection, Line line) {
        SectionAppendResponse appendResponse = new SectionAppendResponse();

        if (hasSections(line)) {
            appendResponse = appendSection(newSection, line);
        }

        if (!hasSections(line)) {
            appendResponse = createFirstSection(line);
        }

        newSection.setLine(line);
        addSection(newSection);

        return appendResponse;
    }

    public List<Station> getStations(Station upStation, Station downStation) {
        List<Section> sortedSections = new ArrayList<>();

        Section firstSection = findSectionWithUpStationByUpStation(upStation)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_BY_UP_STATION));
        sortedSections.add(firstSection);

        Station nextDownStation = firstSection.getDownStation();
        while (!downStation.equals(nextDownStation)) {
            Section nextSection = findNextSectionByDownStation(nextDownStation);
            sortedSections.add(nextSection);
            nextDownStation = nextSection.getDownStation();
        }
        return this.getStations(sortedSections);
    }

    public long getSectionsCount() {
        return this.sections.size();
    }

    public Section removeSectionByStation(Station targetStation) {
        validStationsCountIsOverMinimalSectionSize();
        validRemoveStationIsDownStationInExistLine(targetStation);

        Section lastSection = getLastSection();
        this.remove(lastSection);

        return lastSection;
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    private SectionAppendResponse createFirstSection(Line line) {
        return SectionAppendResponse.builder()
                .upStation(line.getUpStation())
                .downStation(line.getDownStation())
                .build();
    }

    private SectionAppendResponse createSectionAppendResponse(Station upStation, Station downStation) {
        return SectionAppendResponse.builder()
                .upStation(upStation)
                .downStation(downStation)
                .build();
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

    private List<Station> getStations(List<Section> sortedSections) {
        return sortedSections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private Section findNextSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findAny()
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.SECTION_NOT_FOUND_BY_DOWN_STATION));
    }

    private Optional<Section> findSectionWithUpStationByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny();
    }

    private boolean hasSections(Line line) {
        return line.getLineSections().sections.size() > 0;
    }

    private SectionAppendResponse appendSection(Section newSection, Line line) {
        SectionAppendResponse appendResponse = new SectionAppendResponse();

        List<Station> stations = getStations(line.getUpStation(), line.getDownStation());

        validStationInNewSectionIsNotDuplicatedStationInExistLine(newSection, stations);
        validStationInNewSectionIsStationInExistLine(newSection, stations);

        appendResponse = appendUpOrDown(newSection, line, appendResponse);
        appendResponse = appendMiddle(newSection, line, appendResponse);

        return appendResponse;
    }

    private SectionAppendResponse appendUpOrDown(Section newSection, Line line, SectionAppendResponse appendResponse) {
        Station upStationInLine = line.getUpStation();
        Station downStationInLine = line.getDownStation();

        if (upStationInLine.equals(newSection.getDownStation())) {
            appendResponse = createSectionAppendResponse(newSection.getUpStation(), line.getDownStation());
        }

        if (downStationInLine.equals(newSection.getUpStation())) {
            appendResponse = createSectionAppendResponse(line.getUpStation(), newSection.getDownStation());
        }

        return appendResponse;
    }

    private SectionAppendResponse appendMiddle(Section newSection, Line line, SectionAppendResponse appendResponse) {
        appendResponse = appendMiddleDown(newSection, line, appendResponse);
        appendResponse = appendMiddleUp(newSection, line, appendResponse);

        return appendResponse;
    }

    private SectionAppendResponse appendMiddleDown(Section newSection, Line line, SectionAppendResponse appendResponse) {
        Optional<Section> sectionWithDownStationByDownStation = findSectionWithDownStationByDownStation(newSection.getDownStation());
        Optional<Section> sectionWithDownStationByUpStation = findSectionWithDownStationByUpStation(newSection.getUpStation());

        if (sectionWithDownStationByDownStation.isPresent() && sectionWithDownStationByUpStation.isEmpty()) {
            Section existSection = sectionWithDownStationByDownStation.get();
            existSection.changeDownStation(newSection);
            appendResponse = createSectionAppendResponse(line.getUpStation(), line.getDownStation());
        }

        return appendResponse;
    }

    private SectionAppendResponse appendMiddleUp(Section newSection, Line line, SectionAppendResponse appendResponse) {
        Optional<Section> sectionWithUpStationByUpStation = findSectionWithUpStationByUpStation(newSection.getUpStation());
        Optional<Section> sectionWithUpStationByDownStation = findSectionWithUpStationByDownStation(newSection.getDownStation());

        if (sectionWithUpStationByUpStation.isPresent() && sectionWithUpStationByDownStation.isEmpty()) {
            Section existSection = sectionWithUpStationByUpStation.get();
            existSection.changeUpStation(newSection);
            appendResponse = createSectionAppendResponse(line.getUpStation(), line.getDownStation());
        }

        return appendResponse;
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
