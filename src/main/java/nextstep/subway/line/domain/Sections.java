package nextstep.subway.line.domain;

import nextstep.subway.exception.DistanceMaximumException;
import nextstep.subway.exception.NoOtherStationException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.StationDuplicateException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final int LIST_MINIMUM_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public void addSection(Section section) {
        List<StationResponse> stations = getAllStation();
        if (stations.size() == 0) {
            sections.add(section);
            section.update(section.getLine());
            return;
        }

        boolean isExistedUpStation = sections.stream().anyMatch(it -> it.getUpStation().equals(section.getUpStation()));
        boolean isExistedDownStation = sections.stream().anyMatch(it -> it.getDownStation().equals(section.getDownStation()));
        checkExistedStation(isExistedUpStation, isExistedDownStation);

        if (isExistedUpStation) {
            addBeginStation(section);
        }

        if (isExistedDownStation) {
            addLastStation(section);
        }
    }

    public void deleteLastSection(Long stationId) {
        boolean isExistedUpStationId = sections.stream().anyMatch(it -> isEqualsUpStationId(it, stationId));
        boolean isExistedDownStationId = sections.stream().anyMatch(it -> isEqualsDownStationId(it, stationId));

        if (isNotOtherStation()) {
            throw new NoOtherStationException();
        }

        if (isExistedUpStationId && isExistedDownStationId) {
            Line line = getFirstSection().getLine();

            List<Section> removeTargetSections = findAllStation(stationId);

            int totalSectionDistance = firstSectionDistance() + finishSectionDistance();
            Section section = Section.of(line, upStation(), downStation(), totalSectionDistance);

            sections.removeAll(removeTargetSections);
            sections.add(section);
        }

        if (isExistedUpStationId && !isExistedDownStationId) {
            sections.remove(getFirstSection());
        }

        if (!isExistedUpStationId && isExistedDownStationId) {
            sections.remove(getFinishSection());
        }
    }

    public List<StationResponse> getAllStation() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<StationResponse> responses = new ArrayList<>();
        responses.add(StationResponse.of(sections.get(0).getUpStation()));

        sections.stream().map(section -> StationResponse.of(section.getDownStation())).forEach(responses::add);
        return responses;
    }

    private int size() {
        return sections.size();
    }

    private Station upStation() {
        return getFirstSection().getUpStation();
    }

    private Station downStation() {
        return getFinishSection().getDownStation();
    }

    private int firstSectionDistance() {
        return getFirstSection().getDistance();
    }

    private int finishSectionDistance() {
        return getFinishSection().getDistance();
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

    private Section getFinishSection() {
        return sections.get(size() - LIST_MINIMUM_SIZE);
    }

    private boolean isNotOtherStation() {
        return size() == LIST_MINIMUM_SIZE;
    }

    private void checkExistedStation(boolean isExistedUpStation, boolean isExistedDownStation) {
        if (isExistedUpStation && isExistedDownStation) {
            throw new StationDuplicateException();
        }

        if (!isExistedUpStation && !isExistedDownStation) {
            throw new NotFoundException("등록하시려는 역에 상행역과 하행역을 찾을 수 없습니다.");
        }
    }

    private Section findUpSection(Station upStation) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section findDownSection(Station downStation) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private void checkSectionDistance(Section section, Section findSection) {
        if (section.getDistance() >= findSection.getDistance()) {
            throw new DistanceMaximumException();
        }
    }

    private void addBeginStation(Section section) {
        Section findSection = findUpSection(section.getUpStation());
        checkSectionDistance(section, findSection);

        int index = sections.indexOf(findSection);
        Line line = section.getLine();
        section.update(line);

        int sectionDistance = findSection.getDistance() - section.getDistance();
        Section nextSection = Section.of(line, section.getDownStation(), findSection.getDownStation(), sectionDistance);
        sections.set(index, section);
        sections.add(index + 1, nextSection);
    }

    private void addLastStation(Section section) {
        Section findSection = findDownSection(section.getDownStation());
        checkSectionDistance(section, findSection);

        int index = sections.indexOf(findSection);
        Line line = section.getLine();
        section.update(line);

        int sectionDistance = findSection.getDistance() - section.getDistance();
        Section prevSection = Section.of(line, findSection.getUpStation(), section.getUpStation(), sectionDistance);
        sections.set(index, prevSection);
        sections.add(index + 1, section);
    }

    private boolean isEqualsUpStationId(Section section, long stationId) {
        return section.getUpStation().getId().equals(stationId);
    }

    private boolean isEqualsDownStationId(Section section, long stationId) {
        return section.getDownStation().getId().equals(stationId);
    }

    private List<Section> findAllStation(Long stationId) {
        return sections.stream()
                .filter(section -> isEqualsUpStationId(section, stationId)
                        || isEqualsDownStationId(section, stationId))
                .collect(Collectors.toList());
    }
}
