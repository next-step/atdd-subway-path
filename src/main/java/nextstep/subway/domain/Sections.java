package nextstep.subway.domain;


import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private static final String SECTION_ALREADY_EXISTS = "상행역과 하행역 모두 일치힙니다. 이미 등록된 노선입니다.";
    private static final String MATCHED_SECTION_NOT_EXISTS = "상행역과 하행역 둘중 한개라도 일치하는 구간이 없습니다.";
    private static final String CAN_UPDATE_IF_DISTANCE_SHORTER = "역과 역사이에 추가될 경우 새로운 역의 길이가 기존보다 짧아야 합니다.";
    private static final String CAN_DELETE_IF_MORE_THAN_ONE_SECTION_EXISTS = "구간이 1개인 경우 역을 삭제할 수 없습니다";
    private static final int MINIMUM_SIZE_TO_REMOVE = 2;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateForAdding(section);

        if (section.isDownStationEqualTo(findFirstStation())
                || section.isUpStationEqualTo(findLastStation())) {
            sections.add(section);
            return;
        }

        addToMiddle(section);
    }

    private void addToMiddle(Section section) {
        sections.stream()
                .filter(origin -> origin.hasOneMatchedStation(section))
                .findFirst()
                .ifPresent(origin -> {
                    if (!section.hasShorterDistanceThan(origin)) {
                        throw new IllegalArgumentException(CAN_UPDATE_IF_DISTANCE_SHORTER);
                    }
                    sections.add(origin.divideBy(section));
                    sections.add(section);
                    sections.remove(origin);
                });
    }

    private void validateForAdding(Section section) {
        if (findAnyBothMatchedStation(section).isPresent()) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXISTS);
        }

        if (findAnyOneMatchedStation(section).isEmpty()) {
            throw new IllegalArgumentException(MATCHED_SECTION_NOT_EXISTS);
        }
    }

    public void delete(Station station) {
        validateForDeleting(station);

        if (station.equals(findFirstStation())) {
            removeFromFirst(station);
            return;
        }

        if (station.equals(findLastStation())) {
            removeFromLast(station);
            return;
        }

        removeFromMiddle(station);
    }

    private void validateForDeleting(Station station) {
        if (MINIMUM_SIZE_TO_REMOVE > sections.size()) {
            throw new IllegalArgumentException(CAN_DELETE_IF_MORE_THAN_ONE_SECTION_EXISTS);
        }

        if (!hasSectionContainingStation(station)) {
            throw new IllegalArgumentException(MATCHED_SECTION_NOT_EXISTS);
        }
    }

    private void removeFromLast(Station station) {
        sections.stream()
                .filter(i -> i.isDownStationEqualTo(station))
                .findFirst()
                .ifPresent(i -> sections.remove(i));
    }

    private void removeFromFirst(Station station) {
        sections.stream()
                .filter(i -> i.isUpStationEqualTo(station))
                .findFirst()
                .ifPresent(i -> sections.remove(i));
    }

    private void removeFromMiddle(Station station) {
        Section prev = findPreviousSection(station);
        Section next = findNextSection(station);
        sections.add(prev.merge(next));
        sections.remove(prev);
        sections.remove(next);
    }

    private Section findNextSection(Station station) {
        return sections.stream()
                .filter(i -> i.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MATCHED_SECTION_NOT_EXISTS));
    }

    private Section findPreviousSection(Station station) {
        return sections.stream()
                .filter(i -> i.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MATCHED_SECTION_NOT_EXISTS));
    }

    public List<Station> getStations() {
        List<Station> orderedStations = new ArrayList<>();

        Station firstStation = findFirstStation();
        orderedStations.add(firstStation);

        while (findSectionByUpStation(firstStation).isPresent()) {
            Section section = findSectionByUpStation(firstStation).get();
            orderedStations.add(section.getDownStation());
            firstStation = section.getDownStation();
        }

        return Collections.unmodifiableList(orderedStations);
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(i -> i.isUpStationEqualTo(station))
                .findFirst();
    }

    private boolean hasSectionContainingStation(Station station) {
        return sections.stream().anyMatch(i -> i.hasStation(station));
    }

    private Station findFirstStation() {
        List<Station> upStations = findUpStations();
        List<Station> downStations = findDownStations();
        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findAny()
                .orElse(new Station());
    }

    private Station findLastStation() {
        List<Station> upStations = findUpStations();
        List<Station> downStations = findDownStations();
        return downStations.stream()
                .filter(downStation -> !upStations.contains(downStation))
                .findAny()
                .orElse(new Station());
    }

    private List<Station> findUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Optional<Section> findAnyOneMatchedStation(Section section) {
        return sections.stream()
                .filter(origin -> origin.hasOneMatchedStation(section))
                .findAny();
    }

    private Optional<Section> findAnyBothMatchedStation(Section section) {
        return sections.stream()
                .filter(origin -> origin.hasBothMatchedStation(section))
                .findAny();
    }

    public List<Section> getValues() {
        return Collections.unmodifiableList(this.sections);
    }
}
