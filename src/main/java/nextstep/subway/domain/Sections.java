package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
        validateNotMatchedExistingSections(section);

        if (isMatchedSection(s -> s.isMatchedUpStation(section.getUpStation()))) {
            Section existingUpStation = getExistingUpStation(section);
            validateDistanceOfSection(existingUpStation.getDistance(), section.getDistance());
            existingUpStation.updateExistingUpStationAndDowStationSection(section);
        }

        sections.add(section);
    }

    private void validateNotMatchedExistingSections(Section section) {
        List<Station> stations = getStations();
        stations.stream()
                .filter(station -> station.isMatched(section.getUpStation()) || station.isMatched(
                        section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("기존 구간과 등록할 역이 하나 이상 일치하지 않습니다.(상행역:[%s] 하행역:[%s])",
                                section.getUpStation().getName(),
                                section.getDownStation().getName())));
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
                    String.format("기존 구간의 길이는 [%d]로 새로 등록할 구간의 길이는 기존 구간의 길이보다 작아야합니다.(신규 구간 길이:[%d])",
                            existingDistance,
                            newDistance));
        }
    }

    private Section getExistingUpStation(Section section) {
        return sections.stream()
                .filter(s -> s.getUpStation().isMatched(section.getUpStation()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isDuplicateSection(Section section) {
        return sections.stream()
                .anyMatch(s -> s.getUpStation().isMatched(section.getUpStation()) && s.getDownStation()
                        .isMatched(section.getDownStation()));
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
                .filter(section -> isNoneMatchedSection(s -> s.isMatchedDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void deleteSection(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("지하철 노선이 상행 종점역과 하행 종점역만 있는 하나의 구간인 경우, 역을 삭제할 수 없습니다.");
        }

        removeStation(station);
    }

    private void removeStation(Station station) {
        if (!isMatchedSection(s -> s.getUpStation().isMatched(station) || s.getDownStation().isMatched(station))) {
            throw new IllegalArgumentException(String.format("해당 구간에 삭제를 요청한 %s이 존재하지 않습니다.", station.getName()));
        }

        if (isUpEndStation(station)) {
            Section deleteSection = getUpStationInSections(station);
            this.sections.remove(deleteSection);
            return;
        }

        if (isDownEndStation(station)) {
            Section deleteSection = getDownStationInSections(station);
            this.sections.remove(deleteSection);
            return;
        }

        Section deleteSection = getSection(s -> s.isMatchedUpStation(station));
        Section modifiedSection = getSection(s -> s.isMatchedDownStation(station));
        modifiedSection.updateMiddleSection(deleteSection);
        this.sections.remove(deleteSection);
    }

    private Section getSection(Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section getDownStationInSections(Station station) {
        return sections.stream()
                .filter(s -> s.getDownStation().isMatched(station))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isMatchedSection(Predicate<Section> condition) {
        return sections.stream()
                .anyMatch(condition);
    }

    private boolean isNoneMatchedSection(Predicate<Section> condition) {
        return sections.stream()
                .noneMatch(condition);
    }

    private Section getUpStationInSections(Station station) {
        return sections.stream()
                .filter(s -> s.getUpStation().isMatched(station))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isUpEndStation(Station station) {
        return isMatchedSection(s -> s.isMatchedUpStation(station)) && !isMatchedSection(
                s -> s.isMatchedDownStation(station));
    }

    private boolean isDownEndStation(Station station) {
        return isMatchedSection(s -> s.isMatchedDownStation(station)) && !isMatchedSection(
                s -> s.isMatchedUpStation(station));
    }
}
