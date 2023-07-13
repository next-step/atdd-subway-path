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

        if (line.getLineSections().sections.size() > 0) {
            // up이든 down이든 둘중 하나는 연결할 수 있어야됨!
            validStationInNewSectionIsStationInExistLine(newSection, line);
//            validUpStationInNewSectionIsDownStationInExistLine(section, line);
//            validDownStationInNewSectionIsNotDuplicatedInExistLine(section, line);

            // 구간이 중복되는 경우
            validStationInNewSectionIsNotDuplicatedStationInExistLine(newSection, line);

            // 중간에 넣는 경우
//            if (newSection.getDownStation().equals(line.getDownStation()) ||
//                    newSection.getUpStation().equals(line.getUpStation())) {
//                if (this.getTotalDistance() <= newSection.getDistance()) {
//                    throw new SubwayBadRequestException(9999L, "중간에 추가하고자 하는 구간의 길이가 총 길이를 넘을 수 없습니다."); //TODO : constant
//                }
//                appendResponse = SectionAppendResponse.builder()
//                        .upStation(line.getUpStation())
//                        .downStation(line.getDownStation())
//                        .build();
//            }

            // 끝에 늘리는 경우
            if (newSection.getDownStation().equals(line.getUpStation())) {
                appendResponse = SectionAppendResponse.builder()
                        .upStation(newSection.getUpStation())
                        .downStation(line.getDownStation())
                        .build();
            }
            if (newSection.getUpStation().equals(line.getDownStation())) {
                appendResponse = SectionAppendResponse.builder()
                        .upStation(line.getUpStation())
                        .downStation(newSection.getDownStation())
                        .build();
            }
        }

        if (line.getLineSections().sections.size() < 1) {
            appendResponse = SectionAppendResponse.builder()
                    .upStation(line.getUpStation())
                    .downStation(line.getDownStation())
                    .build();
        }
        newSection.setLine(line);
        this.sections.add(newSection);
        return appendResponse;
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

    @Deprecated
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
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.STATION_NOT_FOUND_UP_STATION_IN_LINE_MESSAGE));
    }

    private void validStationInNewSectionIsStationInExistLine(Section section, Line line) {
        List<Station> stations = this.getStations(line.getUpStation(), line.getDownStation());
        Optional<Station> findStation = stations.stream()
                .filter(station -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .findAny();
        if (findStation.isEmpty()) {
            throw new SubwayBadRequestException(SubwayMessage.SECTION_ADD_STATION_NOT_FOUND_ANYONE_MESSAGE);
        }
    }

    private void validStationInNewSectionIsNotDuplicatedStationInExistLine(Section section, Line line) {
        List<Station> stations = this.getStations(line.getUpStation(), line.getDownStation());
        Optional<Station> findUpStation = stations.stream()
                .filter(station -> station.equals(section.getUpStation()))
                .findAny();
        Optional<Station> findDownStation = stations.stream()
                .filter(station -> station.equals(section.getDownStation()))
                .findAny();
        if (findUpStation.isPresent() && findDownStation.isPresent()) {
            throw new SubwayBadRequestException(SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE_MESSAGE.getCode(),
                    SubwayMessage.STATION_IS_ALREADY_EXIST_IN_LINE_MESSAGE.getFormatMessage(section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

//    private void validUpStationInNewSectionIsDownStationInExistLine(Section section, Line line) {
//        if (!line.getDownStation().equals(section.getUpStation())) {
//            throw new SubwayBadRequestException(SubwayMessage.DOWN_STATION_NOT_MATCH_WITH_UP_STATION);
//        }
//    }
//
//    private void validDownStationInNewSectionIsNotDuplicatedInExistLine(Section section, Line line) {
//        List<Station> stationsInLine = line.getStations();
//        stationsInLine.stream()
//                .filter(s -> s.equals(section.getDownStation()))
//                .findAny()
//                .ifPresent(e -> {
//                    throw new SubwayBadRequestException(SubwayMessage.SECTION_ADD_STATION_DUPLICATION_VALID_MESSAGE);
//                });
//    }

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
