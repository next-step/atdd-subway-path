package nextstep.subway.line.domain;

import nextstep.subway.line.exception.AlreadyExistInLineException;
import nextstep.subway.line.exception.IllegalSectionException;
import nextstep.subway.line.exception.NotExistInLineException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private static final String LONG_SECTION = "새로운 구간의 길이가 너무 길어서 등록할 수 없습니다.";
    private static final String CANT_DELETE_ONE_SECTION = "구간이 한개라서 역을 삭제할 수 없습니다.";
    private static final String NOT_INCLUDED_STATION = "노선에 없는 역을 제거할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void addSections(Section section) {
        if (isFirstSection()) {
            sections.add(section);
            return;
        }

        boolean isUpStationExisted = getStations().stream().anyMatch(it -> it == section.getUpStation());
        boolean isDownStationExisted = getStations().stream().anyMatch(it -> it == section.getDownStation());
        checkEqualUpAndDownStations(isUpStationExisted, isDownStationExisted);
        checkNotIncludedStations(isUpStationExisted, isDownStationExisted);

        addEqualUpStation(section);
        addEqualDownStation(section);
        sections.add(section);
    }

    private void checkEqualUpAndDownStations(boolean isUpStationExisted, boolean isDownStationExisted) {
        if (isUpStationExisted && isDownStationExisted) {
            throw new AlreadyExistInLineException();
        }
    }

    private void checkNotIncludedStations(boolean isUpStationExisted, boolean isDownStationExisted) {
        if (!isUpStationExisted && !isDownStationExisted) {
            throw new NotExistInLineException();
        }
    }

    private void addEqualUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    checkEqualDistance(section, oldSection);
                    sections.add(makeNewAfterSection(section, oldSection));
                    sections.remove(oldSection);
                });
    }

    private void addEqualDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    checkEqualDistance(section, oldSection);
                    sections.add(makeNewBeforeSection(section, oldSection));
                    sections.remove(oldSection);
                });
    }

    private void checkEqualDistance(Section newSection, Section oldSection) {
        if (oldSection.getDistance() - newSection.getDistance() <= 0) {
            throw new IllegalSectionException(LONG_SECTION);
        }
    }

    private Section makeNewBeforeSection(Section section, Section oldSection) {
        return new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(),
                oldSection.getDistance() - section.getDistance());
    }

    private Section makeNewAfterSection(Section section, Section oldSection) {
        return new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(),
                oldSection.getDistance() - section.getDistance());
    }

    private boolean isFirstSection() {
        return getStations().size() == 0;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public void removeSection(Station station) {
        validateSectionForRemove(station);

        // remove before section
        if (isStationLocatedBeforeSection(station)) {
            removeBeforeSection(station);
            return;
        }

        // remove after section
        if (isStationLocatedAfterSection(station)) {
            removeAfterSection(station);
            return;
        }

        // remove middle
        removeMiddleSection(station);
    }

    private void validateSectionForRemove(Station station) {
        if (hasLastSection()) {
            throw new IllegalSectionException(CANT_DELETE_ONE_SECTION);
        }
        if (hasNotStation(station)) {
            throw new IllegalSectionException(NOT_INCLUDED_STATION);
        }
    }

    private boolean hasLastSection() {
        return sections.size() <= 1;
    }

    private boolean hasNotStation(Station station) {
        return !findBeforeSectionOptional(station).isPresent() && !findAfterSectionOptional(station).isPresent();
    }

    private boolean isStationLocatedBeforeSection(Station station) {
        return findBeforeSectionOptional(station).isPresent() && !findAfterSectionOptional(station).isPresent();
    }

    private boolean isStationLocatedAfterSection(Station station) {
        return !findBeforeSectionOptional(station).isPresent() && findAfterSectionOptional(station).isPresent();
    }

    private Optional<Section> findBeforeSectionOptional(Station station) {
        return sections.stream()
                .filter(s -> s.getDownStation() == station)
                .findFirst();
    }

    private Optional<Section> findAfterSectionOptional(Station station) {
        return sections.stream()
                .filter(s -> s.getUpStation() == station)
                .findFirst();
    }

    private void removeAfterSection(Station station) {
        sections.removeIf(s -> s.getUpStation() == station);
    }

    private void removeBeforeSection(Station station) {
        sections.removeIf(s -> s.getDownStation() == station);
    }

    private void removeMiddleSection(Station station) {
        Section beforeSection = findBeforeSectionOptional(station).get();
        Section afterSection = findAfterSectionOptional(station).get();
        int distance = beforeSection.getDistance() + afterSection.getDistance();

        sections.remove(beforeSection);
        sections.remove(afterSection);
        sections.add(new Section(beforeSection.getLine(),
                beforeSection.getUpStation(), afterSection.getDownStation(), distance));
    }

    public List<Section> getSections() {
        return sections;
    }
}
