package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateDuplicationSection(section);

        if (isMatchedUpStation(section)) {
            Section existingUpStation = getExistingUpStation(section);
            validateDistanceOfSection(existingUpStation.getDistance(), section.getDistance());
            existingUpStation.updateExistingUpStationAndDowStationSection(section);
        }

        sections.add(section);
    }

    private void validateDuplicationSection(Section section) {
        if (isDuplicateSection(section)) {
            throw new IllegalArgumentException(
                    String.format("이미 등록된 구간의 역입니다.(상행역:[%s] 하행역:[%s])", section.getUpStation().getName(),
                            section.getDownStation().getName()));
        }
    }

    private void validateDistanceOfSection(int existingDistance, int newDistance) {
        if (existingDistance <= newDistance) {
            throw new IllegalArgumentException(
                    String.format("기존 구간의 길이는 [%d]로 새로 등록할 구간의 길이는 기존 구간의 길이보다 작아야합니다.(신규 구간 길이:[%d])", existingDistance,
                            newDistance));
        }
    }

    private Section getExistingUpStation(Section section) {
        return sections.stream()
                .filter(s -> s.getUpStation().isMatched(section.getUpStation()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isMatchedUpStation(Section section) {
        return sections.stream()
                .anyMatch(s -> s.getUpStation().isMatched(section.getUpStation()));
    }

    private boolean isDuplicateSection(Section section) {
        return sections.stream()
                .anyMatch(s -> s.getUpStation().isMatched(section.getUpStation()) && s.getDownStation()
                        .isMatched(section.getDownStation()));
    }

    private boolean stationsContain(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    private Section getDownEndStation() {
        int downEndStationIndex = getDownEndStationIndex();
        return sections.get(downEndStationIndex);
    }

    private int getDownEndStationIndex() {
        return sections.size() - 1;
    }

    public List<Section> getSections() {
        List<Section> orderedSections = new ArrayList<>();

        if (sections.isEmpty()) {
            return orderedSections;
        }

        Section findingSection = getFirstSection();

        while (findingSection != null) {
            orderedSections.add(findingSection);
            findingSection = getNextSection(findingSection.getDownStation());
        }

        return orderedSections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if (sections.isEmpty()) {
            return stations;
        }

        Section findingSection = getFirstSection();
        stations.add(findingSection.getUpStation());

        while (findingSection != null) {
            stations.add(findingSection.getDownStation());
            findingSection = getNextSection(findingSection.getDownStation());
        }

        return stations;
    }

    private Section getNextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().isMatched(station))
                .findAny()
                .orElse(null);
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(section -> isUpStation(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isUpStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().isMatched(station));
    }

    public void deleteSection(Station station) {
        Section downEndStation = getDownEndStation();
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("지하철 노선이 상행 종점역과 하행 종점역만 있는 하나의 구간인 경우, 역을 삭제할 수 없습니다.");
        }

        if (!downEndStation.isMatchedStationId(station)) {
            throw new IllegalArgumentException("지하철 노선의 하행 종점역만 삭제 할 수 있습니다.");
        }
        removeDownEndSection();
    }

    private void removeDownEndSection() {
        sections.remove(getDownEndStationIndex());
    }
}
