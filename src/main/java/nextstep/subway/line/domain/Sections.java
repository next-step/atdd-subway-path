package nextstep.subway.line.domain;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    public static final String DISTANCE_EXCEPTION_MESSAGE = "기존 구간의 길이보다 작은 길이의 구간만 추가할 수 있습니다.";
    public static final String BOTH_EXIST_EXCEPTION_MESSAGE = "상행역과 하행역 모두 이미 노선에 등록되어 있습니다.";
    public static final String BOTH_NOT_EXIST_EXCEPTION_MESSAGE = "상행역과 하행역 중 최소 하나는 노선에 등록되어 있어야 합니다.";
    public static final String NON_EXIST_DELETE_EXCEPTION_MESSAGE = "노선에 등록된 역만 삭제할 수 있습니다.";
    public static final String ONE_SECTION_DELETE_EXCEPTION_MESSAGE = "노선에 구간이 하나인 경우 역 삭제를 할 수 없습니다.";

    public static final int VALIDATE_DELETE_SECTION_CRITERIA = 1;
    public static final int VALIDATE_DISTANCE_CRITERIA = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean isExistUpStation = isExistStation(section.getUpStation());
        boolean isExistDownStation = isExistStation(section.getDownStation());
        validateAddSection(isExistUpStation, isExistDownStation);

        if(isExistUpStation) {
            addSectionBasedOnUpStation(section);
        }

        if(isExistDownStation) {
            addSectionBasedOnDownStation(section);
        }
    }

    private void validateAddSection(boolean isExistUpStation, boolean isExistDownStation) {
        if(isExistUpStation && isExistDownStation) {
            throw new BadRequestException(BOTH_EXIST_EXCEPTION_MESSAGE);
        }

        if(!isExistUpStation && !isExistDownStation) {
            throw new BadRequestException(BOTH_NOT_EXIST_EXCEPTION_MESSAGE);
        }
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private void addSectionBasedOnUpStation(Section section) {
        Section findSection = findSectionByUpStation(section.getUpStation());
        if(Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        Section nextSection = createSectionForAdd(findSection, section, true);

        int index = sections.indexOf(findSection);
        sections.set(index, section);
        sections.add(index + 1, nextSection);
    }

    private void addSectionBasedOnDownStation(Section section) {
        Section findSection = findSectionByDownStation(section.getDownStation());
        if(Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        Section prevSection = createSectionForAdd(findSection, section, false);

        int index = sections.indexOf(findSection);
        sections.set(index, prevSection);
        sections.add(index + 1, section);
    }

    private Section createSectionForAdd(Section findSection, Section section, boolean isBasedOnUpStation) {
        Line line = findSection.getLine();
        int distance = findSection.getDistance() - section.getDistance();
        validateDistance(distance);

        if(isBasedOnUpStation) {
            return Section.builder()
                    .line(line)
                    .upStation(section.getDownStation())
                    .downStation(findSection.getDownStation())
                    .distance(distance)
                    .build();
        }

        return Section.builder()
                .line(line)
                .upStation(findSection.getUpStation())
                .downStation(section.getUpStation())
                .distance(distance)
                .build();
    }


    public void validateDistance(int distance) {
        if(distance <= VALIDATE_DISTANCE_CRITERIA) {
            throw new BadRequestException(DISTANCE_EXCEPTION_MESSAGE);
        }
    }

    private boolean isExistStation(Station station) {
        return getAllStation().contains(station);
    }

    public List<Station> getAllStation() {
        if(sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = sections.stream()
                .filter(section -> Objects.isNull(findSectionByDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        stations.add(firstSection.getUpStation());
        addStationInOrder(stations, firstSection.getDownStation());
        return stations;
    }

    private void addStationInOrder(List<Station> stations, Station downStation) {
        stations.add(downStation);
        Section section = findSectionByUpStation(downStation);

        if(Objects.nonNull(section)) {
            addStationInOrder(stations, section.getDownStation());
        }
    }

    public void remove(Station station) {
        Section prevSection = findSectionByDownStation(station);
        Section nextSection = findSectionByUpStation(station);

        validateRemoveSection(station);

        if(isLastStation(prevSection, nextSection)) {
            sections.remove(prevSection);
            return;
        }

        if(isFirstStation(prevSection, nextSection)) {
            sections.remove(nextSection);
            return;
        }

        sections.removeAll(Arrays.asList(prevSection, nextSection));
        sections.add(createSectionForRemove(prevSection, nextSection));
    }

    private void validateRemoveSection(Station station) {
        if(sections.size() <= VALIDATE_DELETE_SECTION_CRITERIA) {
            throw new BadRequestException(ONE_SECTION_DELETE_EXCEPTION_MESSAGE);
        }

        if(!isExistStation(station)) {
            throw new BadRequestException(NON_EXIST_DELETE_EXCEPTION_MESSAGE);
        }
    }

    private boolean isLastStation(Section prevSection, Section nextSection) {
        return Objects.isNull(nextSection) && Objects.nonNull(prevSection);
    }

    private boolean isFirstStation(Section prevSection, Section nextSection) {
        return Objects.isNull(prevSection) && Objects.nonNull(nextSection);
    }

    private Section createSectionForRemove(Section prevSection, Section nextSection) {
        Line line = prevSection.getLine();
        int distance = prevSection.getDistance() + nextSection.getDistance();
        Station upStation = prevSection.getUpStation();
        Station downStation = nextSection.getDownStation();

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

}
