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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@NoArgsConstructor
public class LineSections {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section, Line line) {
        if (line.getLineSections().sections.size() > 1) {
            validUpStationInNewSectionIsDownStationInExistLine(section, line);
            validDownStationInNewSectionIsNotDuplicatedInExistLine(section, line);
        }
        section.setLine(line);
        this.sections.add(section);
    }

    public List<Station> getStations(Station upStation, Station downStation) {
        List<Section> sortedSections = new ArrayList<>();

        Section firstSection = findSectionByUpStation(upStation);
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
                .orElseThrow(() -> new SubwayNotFoundException(9999L, "노선에 등록된 하행역이 구간에 없습니다."));
    }

    private Section findSectionByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new SubwayNotFoundException(9999L, "노선에 등록된 상행역이 구간에 없습니다."));
    }

    private void validUpStationInNewSectionIsDownStationInExistLine(Section section, Line line) {
        if (!line.getDownStation().equals(section.getUpStation())) {
            throw new SubwayBadRequestException(SubwayMessage.DOWN_STATION_NOT_MATCH_WITH_UP_STATION);
        }
    }

    private void validDownStationInNewSectionIsNotDuplicatedInExistLine(Section section, Line line) {
        List<Station> stationsInLine = line.getStations();
        stationsInLine.stream()
                .filter(s -> s.equals(section.getDownStation()))
                .findAny()
                .ifPresent(e -> {
                    throw new SubwayBadRequestException(SubwayMessage.ADD_SECTION_STATION_DUPLICATION_VALID_MESSAGE);
                });
    }

    private void validStationsCountIsOverMinimalSectionSize() {
        if (this.getSectionsCount() < MINIMAL_SECTION_SIZE) {
            throw new SubwayBadRequestException(SubwayMessage.DOWN_STATION_MINIMAL_VALID_MESSAGE.getCode(),
                    SubwayMessage.DOWN_STATION_MINIMAL_VALID_MESSAGE.getFormatMessage(MINIMAL_SECTION_SIZE));
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
