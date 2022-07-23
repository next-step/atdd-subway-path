package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Arrays.*;

@Embeddable
public class Sections {

    private static final int DELETE_AVAILABLE_SIZE = 2;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (alreadyConnectStation(section)) {
            throw new IllegalArgumentException("이미 등록된 역은 등록할 수 없어요.");
        }

        if (nonExistConnectStation(section)) {
            throw new IllegalArgumentException("연결할 수 있는 역이 없어요.");
        }

        if (isAddFirstOrLastSection(section)) {
            this.sections.add(section);
            return;
        }

        if (hasEqualsUpStation(section)) {
            Section originalSection = getSectionByCondition(s -> s.equalsUpStation(section.getUpStation()));
            originalSection.updateUpStationToSectionDownStation(section);
        }

        if (hasEqualsDownStation(section)) {
            Section originalSection = getSectionByCondition(s -> s.equalsDownStation(section.getDownStation()));
            originalSection.updateDownStationToSectionUpStation(section);
        }

        this.sections.add(section);
    }

    public Section get(int index) {
        return this.sections.get(index);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Station> sortedStations = new LinkedHashSet<>();
        Section section = getFirstSection();

        while (section != null) {
            sortedStations.addAll(asList(section.getUpStation(), section.getDownStation()));
            section = getNextSection(section.getDownStation());
        }

        return List.copyOf(sortedStations);
    }

    public int size() {
        return this.sections.size();
    }


    public void deleteStation(Station station) {
        if (sections.size() < DELETE_AVAILABLE_SIZE) {
            throw new IllegalArgumentException("구간은 두개 이상부터 제거가 가능해요");
        }

        if (isNotExistStation(station)) {
            throw new IllegalArgumentException("삭제할 역이 없어요");
        }

        if (isEndOfUpStation(station)) {
            Section deleteSection = getSectionByCondition(section -> section.equalsUpStation(station));
            this.sections.remove(deleteSection);
            return;
        }

        if (isEndOfDownStation(station)) {
            Section deleteSection = getSectionByCondition(section -> section.equalsDownStation(station));
            this.sections.remove(deleteSection);
            return;
        }


        Section deleteSection = getSectionByCondition(section -> section.equalsUpStation(station));
        Section updateSection = getSectionByCondition(section -> section.equalsDownStation(station));

        updateSection.updateDownStationToSectionDownStationAndAddDistance(deleteSection);
        this.sections.remove(deleteSection);
    }

    private Section getFirstSection() {
        return this.sections.stream()
                .filter(section -> anyNonMatchSection(s -> s.equalsDownStation(section.getUpStation())))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

    private Section getNextSection(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.equalsUpStation(downStation))
                .findAny()
                .orElse(null);
    }

    private boolean isNotExistStation(Station station) {
        return anyNonMatchSection(section -> section.equalsUpStation(station) || section.equalsDownStation(station));
    }

    private boolean isEndOfDownStation(Station station) {
        return anyMatchSection(section -> section.equalsDownStation(station)) && anyNonMatchSection(section -> section.equalsUpStation(station));
    }

    private boolean isEndOfUpStation(Station station) {
        return anyMatchSection(section -> section.equalsUpStation(station)) && anyNonMatchSection(section -> section.equalsDownStation(station));
    }

    private boolean alreadyConnectStation(Section section) {
        return anyMatchSection(s -> s.anyEqualsStation(section.getUpStation())) && anyMatchSection(s -> s.anyEqualsStation(section.getDownStation()));
    }

    private boolean nonExistConnectStation(Section section) {
        return anyNonMatchSection(s -> s.anyEqualsStation(section.getUpStation()) || s.anyEqualsStation(section.getDownStation()));
    }

    private boolean isAddFirstOrLastSection(Section section) {
        return anyMatchSection(s -> s.equalsDownStation(section.getUpStation()) || s.equalsUpStation(section.getDownStation()));
    }

    private boolean hasEqualsDownStation(Section section) {
        return anyMatchSection(s -> s.equalsDownStation(section.getDownStation()));
    }

    private boolean hasEqualsUpStation(Section section) {
        return anyMatchSection(s -> s.equalsUpStation(section.getUpStation()));
    }

    private Section getSectionByCondition(Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElseThrow(AssertionError::new);
    }


    private boolean anyNonMatchSection(Predicate<Section> condition) {
        return !anyMatchSection(condition);
    }

    private boolean anyMatchSection(Predicate<Section> condition) {
        return sections.stream()
                .anyMatch(condition);
    }

}
