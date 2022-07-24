package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.SectionMinimumLimitException;
import nextstep.subway.exception.SectionNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSections(final Line line, final Station upStation, final Station downStation, final int distance) {
        final Section newSection = new Section(line, upStation, downStation, distance);
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        if (notExistStation(newSection.getUpStation()) && notExistStation(newSection.getDownStation())) {
            throw new IllegalStateException("상행, 하행역이 모두 포함되어있지 않습니다.");
        }

        if (isAlreadyRegistered(newSection)) {
            throw new IllegalStateException("이미 등록되어있는 구간입니다.");
        }

        if (isMatchCondition(isMiddleSection(newSection))) {
            Section originalSection = getSectionByCondition(isMiddleSection(newSection));
            originalSection.addMiddleSection(newSection);
        }

        this.sections.add(newSection);
    }

    private boolean notExistStation(final Station station) {
        return getStations().stream()
            .noneMatch(registerStation -> registerStation.equals(station));
    }

    private Predicate<Section> isMiddleSection(final Section newSection) {
        return section -> section.isSameUpStation(newSection.getUpStation()) ||
            section.isSameDownStation(newSection.getDownStation());
    }

    private boolean isAlreadyRegistered(Section newSection) {
        return isMatchCondition(alreadyExistStations(newSection));
    }

    private boolean isMatchCondition(Predicate<Section> matchCondition) {
        return sections.stream().anyMatch(matchCondition);
    }

    private Predicate<Section> alreadyExistStations(final Section newSection) {
        return section -> section.getUpStation().equals(newSection.getUpStation()) &&
            section.getDownStation().equals(newSection.getDownStation());
    }

    private Section getSectionByCondition(Predicate<Section> matchCondition) {
        return sections.stream()
            .filter(matchCondition)
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);
    }

    public List<Section> sections() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Section> sortedSections = new ArrayList<>();
        Section firstSection = getFirstSection();
        sortedSections.add(firstSection);

        for (int i = 0; i < sections.size(); i++) {
            Section nextSection = getNextSection(sortedSections.get(i));
            if (Objects.isNull(nextSection)) {
                break;
            }
            sortedSections.add(nextSection);
        }
        return sortedSections;
    }

    public Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new NoSuchElementException("저장 된 구간 정보가 없습니다.");
        }
        return sections.stream()
            .filter(this::isFirstSection)
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        return sections().stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeStations(final Station station) {
        notRegisteredValidation();

        minimumSizeValidation();

        // 맨앞의 역 제거
        final Section section = getFirstSection();
        if (section.isSameUpStation(station)) {
            sections.remove(section);
            return;
        }

        // 가운데 역 제거
        for (Section currentSection : sections) {
            final Section nextSection = getNextSection(currentSection);
            if (Objects.nonNull(nextSection) && isMiddleSection(currentSection, nextSection, station)) {
                currentSection.removeMiddleSection(nextSection.getDistance(), nextSection.getDownStation());
                sections.remove(nextSection);
                return;
            }
        }

        downStationValidation(station);
        sections.remove(getLastIndex());
    }

    private boolean isMiddleSection(final Section currentSection, final Section nextSection, final Station removeStation) {
        return currentSection.isSameDownStation(removeStation) &&
            nextSection.isSameUpStation(removeStation);
    }

    private void notRegisteredValidation() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("등록된 구간이 없습니다.");
        }
    }

    private void downStationValidation(final Station station) {
        if (isNotEqualLastDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역 정보가 다릅니다.");
        }
    }

    private void minimumSizeValidation() {
        if (sections.size() == 1) {
            throw new SectionMinimumLimitException();
        }
    }

    private boolean isNotEqualLastDownStation(final Station station) {
        return !sections.get(getLastIndex()).getDownStation().equals(station);
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public boolean isFirstSection(final Section section) {
        return sections.stream()
            .noneMatch(currentSection -> currentSection.getDownStation().equals(section.getUpStation()));
    }

    private Section getNextSection(final Section section) {
        return sections.stream()
            .filter(thisSection -> section.getDownStation().equals(thisSection.getUpStation()))
            .findFirst().orElse(null);
    }

}
