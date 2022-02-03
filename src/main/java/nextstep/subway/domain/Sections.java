package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Embeddable
public class Sections {
    private static final String ALREADY_EXIST_SECTION_MESSAGE = "이미 존재하는 구간입니다.";
    private static final String NO_FIRST_STATION_MESSAGE = "상행 종점역이 존재하지 않습니다.";
    private static final String NO_LAST_STATION_MESSAGE = "하행 종점역이 존재하지 않습니다.";
    private static final String CANNOT_DELETE_MIN_SIZE_SECTION_MESSAGE = "구간이 1개일 때는 제거할 수 없습니다.";
    private static final int LAST_INDEX_VALUE = 1;
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        if (isAlreadyExistSection(section) || nonExistStation(section)) {
            throw new IllegalArgumentException(ALREADY_EXIST_SECTION_MESSAGE);
        }
        
        if (isMiddleSection(section)) {
            addMiddleSection(section);
            return;
        }

        sectionList.add(section);
    }

    private boolean nonExistStation(Section section) {
        return !sectionList.isEmpty() && sectionList.stream()
                .noneMatch(otherSection ->
                        section.getUpStation().equals(otherSection.getUpStation())
                        || section.getUpStation().equals(otherSection.getDownStation())
                        || section.getDownStation().equals(otherSection.getUpStation())
                        || section.getDownStation().equals(otherSection.getDownStation())
                );
    }

    private boolean isAlreadyExistSection(Section section) {
        return sectionList.stream()
                .anyMatch(section::equalsUpAndDownStation);
    }

    private boolean isMiddleSection(Section section) {
        return sectionList.stream()
                .anyMatch(otherSection ->
                        section.getUpStation().equals(otherSection.getUpStation())
                                || section.getDownStation().equals(otherSection.getDownStation()));
    }

    private void addMiddleSection(Section section) {
        Optional<Section> sameUpStationSection = findSectionBy(section::hasSameUpStation);
        if (sameUpStationSection.isPresent()) {
            Section beforeSection = sameUpStationSection.get();
            Section changedSection = beforeSection.changeUpStation(section);
            replaceSection(beforeSection, changedSection);
            sectionList.add(section);
            return;
        }

        Optional<Section> sameDownStationSection = findSectionBy(section::hasSameDownStation);
        if (sameDownStationSection.isPresent()) {
            Section beforeSection = sameDownStationSection.get();
            Section changedSection = beforeSection.changeDownStation(section);
            replaceSection(beforeSection, changedSection);
            sectionList.add(section);
            return;
        }
    }

    private void replaceSection(Section beforeSection, Section changedSection) {
        int index = sectionList.indexOf(beforeSection);
        sectionList.set(index, changedSection);
    }

    private Optional<Section> findSectionBy(Predicate<Section> sameStationSectionPredicate) {
        return sectionList.stream()
                .filter(sameStationSectionPredicate)
                .findFirst();
    }

    private List<Station> makeStationList(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        addNextSection(section, stations);
        return stations;
    }

    private void addNextSection(Section section, List<Station> stations) {
        stations.add(section.getDownStation());

        Optional<Section> nextSection = sectionList.stream()
                .filter(section::isPreviousSection)
                .findFirst();

        nextSection.ifPresent(sectionNext -> addNextSection(nextSection.get(), stations));
    }

    private Section findFirstSection() {
        return sectionList.stream()
                .filter(this::firstSectionCondition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_FIRST_STATION_MESSAGE));
    }

    private boolean firstSectionCondition(Section section) {
        return sectionList.stream()
                .noneMatch(section::isNextSection);
    }

    public void deleteSection(Station station) {
        if (sectionList.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException(CANNOT_DELETE_MIN_SIZE_SECTION_MESSAGE);
        }

        if (isFirstStation(station)) {
            sectionList.remove(findFirstSection());
            return;
        }

        if (isLastStation(station)) {
            sectionList.remove(findLastSection());
            return;
        }

        removeMiddleSection(station);
    }

    private void removeMiddleSection(Station station) {
        Section upSection = findSectionBy(section -> section.getDownStation().equals(station))
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 하행역인 구간이 존재하지 않습니다."));

        Section downSection = findSectionBy(section -> section.getUpStation().equals(station))
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 상행역인 구간이 존재하지 않습니다."));

        combineSections(upSection, downSection);
    }

    private void combineSections(Section upSection, Section downSection) {
        int index = sectionList.indexOf(upSection);
        Section combinedUpSection = upSection.combine(downSection);
        sectionList.set(index, combinedUpSection);
        sectionList.remove(downSection);
    }

    private Section findLastSection() {
        return sectionList.stream()
                .filter(this::lastSectionCondition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_LAST_STATION_MESSAGE));
    }

    private boolean lastSectionCondition(Section section) {
        return sectionList.stream()
                .noneMatch(section::isPreviousSection);
    }

    private boolean isLastStation(Station station) {
        return findLastSection().getDownStation().equals(station);
    }

    private boolean isFirstStation(Station station) {
        return findFirstSection().getUpStation().equals(station);
    }

    private Station lastStation() {
        return sectionList.get(lastIndex()).getDownStation();
    }

    private int lastIndex() {
        return sectionList.size() - LAST_INDEX_VALUE;
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public List<Station> getStationList() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        Section firstSection = findFirstSection();
        List<Station> stations = makeStationList(firstSection);

        return Collections.unmodifiableList(stations);
    }
}
