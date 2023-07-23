package nextstep.subway.domain.vo;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.IllegalSectionStationException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Embeddable
public class Sections {

    private static final int MINIMUM_SIZE = 1;

    @JoinColumn(name = "section_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getValue() {
        return sections;
    }

    public void add(Section newSection) {
        Optional<Section> newDownStationInMiddleOfSection = this.getStationSection(section -> section.isUpStation(newSection.getUpStation()));
        if (newDownStationInMiddleOfSection.isPresent()) {
            this.splitSectionUpStationBase(newDownStationInMiddleOfSection.get(), newSection);
            return;
        }

        Optional<Section> newUpStationInMiddleOfSection = this.getStationSection(section -> section.isDownStation(newSection.getDownStation()));
        if (newUpStationInMiddleOfSection.isPresent()) {
            this.splitSectionDownStationBase(newUpStationInMiddleOfSection.get(), newSection);
            return;
        }

        this.sections.add(newSection);
    }

    private void splitSectionUpStationBase(Section oldSection, Section newSection) {
        this.sections.remove(oldSection);
        Long newSectionDistance = newSection.getDistance();
        Section newUpSection = Section.of(newSection.getUpStation(), newSection.getDownStation(), newSectionDistance);
        Section newDownSection = Section.of(newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSectionDistance);
        this.sections.add(newUpSection);
        this.sections.add(newDownSection);
    }

    private void splitSectionDownStationBase(Section oldSection, Section newSection) {
        this.sections.remove(oldSection);
        Long newSectionDistance = newSection.getDistance();
        Section newUpSection = Section.of(oldSection.getUpStation(), newSection.getUpStation(), newSectionDistance);
        Section newDownSection = Section.of(newSection.getUpStation(), newSection.getDownStation(), oldSection.getDistance() - newSectionDistance);
        this.sections.add(newUpSection);
        this.sections.add(newDownSection);
    }

    public Long sumOfDistance() {
        return this.sections.stream().mapToLong(Section::getDistance).sum();
    }

    public boolean isMinimumSize() {
        return this.sections.size() == MINIMUM_SIZE;
    }

    public void remove(Station targetStation) {
        if (isLastStationOfLine(targetStation)) {
            removeLastStationOfLine(targetStation);
            return;
        }

        removeMiddleStationOfLine(targetStation);
    }

    private boolean isLastStationOfLine(Station targetStation) {
        Station startOfLine = getStartOfLine();
        Station endOfLine = getEndOfLine();
        return Objects.equals(targetStation, startOfLine)
                || Objects.equals(targetStation, endOfLine);
    }

    private void removeLastStationOfLine(Station targetStation) {
        Station startOfLine = getStartOfLine();
        Station endOfLine = getEndOfLine();
        this.sections.stream()
                .filter(section -> Objects.equals(targetStation, startOfLine)
                        ? section.isUpStation(startOfLine)
                        : section.isDownStation(endOfLine))
                .findAny()
                .ifPresent(this.sections::remove);
    }

    private void removeMiddleStationOfLine(Station targetStation) {
        Section upStationSection = this.getStationSection(section -> section.isDownStation(targetStation))
                .orElseThrow(IllegalSectionStationException::new);
        Section downStationSection = this.getStationSection(section -> section.isUpStation(targetStation))
                .orElseThrow(IllegalSectionStationException::new);

        Station newUpStation = upStationSection.getUpStation();
        Station newDownStation = downStationSection.getDownStation();
        long newDistance = upStationSection.getDistance() + downStationSection.getDistance();
        Section newUpSection = Section.of(newUpStation, newDownStation, newDistance);

        this.sections.add(newUpSection);
        this.sections.remove(upStationSection);
        this.sections.remove(downStationSection);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(getStartOfLine());
        appendAllMiddleStationsInSections(stations);
        stations.add(getEndOfLine());
        return stations;
    }

    private void appendAllMiddleStationsInSections(List<Station> stations) {
        this.sections.forEach(targetSection ->
                this.sections.stream()
                        .filter(pairSection -> targetSection.isDownStation(pairSection.getUpStation()))
                        .findFirst()
                        .map(Section::getUpStation)
                        .ifPresent(stations::add));
    }

    public Station getStartOfLine() {
        return this.sections.stream()
                .filter(targetSection -> this.sections.stream()
                        .noneMatch(pairSection -> targetSection.isUpStation(pairSection.getDownStation()))
                )
                .findFirst()
                .map(Section::getUpStation)
                .orElseThrow(IllegalSectionStationException::new);
    }

    public Station getEndOfLine() {
        return this.sections.stream()
                .filter(targetSection -> this.sections.stream()
                        .noneMatch(pairSection -> targetSection.isDownStation(pairSection.getUpStation()))
                )
                .findFirst()
                .map(Section::getDownStation)
                .orElseThrow(IllegalSectionStationException::new);
    }

    public boolean canNotAdd(Section section) {
        return !canAdd(section);
    }

    private boolean canAdd(Section newSection) {
        return this.sections.stream()
                .filter(section -> section.isDifferentAs(newSection))
                .filter(section -> section.isExpandable(newSection))
                .anyMatch(section -> this.isLastSectionOfLine(newSection)
                        || !(this.isLastSectionOfLine(newSection) || section.isEqualsOrShorterThan(newSection)));
    }

    private boolean isLastSectionOfLine(Section section) {
        return Objects.equals(getStartOfLine(), section.getDownStation())
                || Objects.equals(getEndOfLine(), section.getUpStation());
    }

    public Optional<Section> getStationSection(Predicate<Section> condition) {
        return this.sections.stream().filter(condition).findFirst();
    }
}
