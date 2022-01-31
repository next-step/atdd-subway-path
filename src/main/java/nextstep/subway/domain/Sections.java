package nextstep.subway.domain;

import nextstep.subway.exception.*;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    private static final int SECTION_SIZE_LIMIT = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        boolean doesUpStationExist = doesStationExist(newSection.getUpStation());
        boolean doesDownStationExist = doesStationExist(newSection.getDownStation());

        validateAddingSectionOrElseThrow(newSection, doesUpStationExist, doesDownStationExist);

        if (doesDownStationExist) {
            appendSection(newSection);
        }
        if (doesUpStationExist) {
            prependSection(newSection);
        }
    }

    private void appendSection(Section newSection) {
        if (isLastUpStation(newSection.getDownStation())) {
            addSectionOntoLastUpStation(newSection);
            return;
        }
        addSectionBetweenSectionsBasedOnDownStation(newSection);
    }

    private void prependSection(Section newSection) {
        if (isLastDownStation(newSection.getUpStation())) {
            addSectionOntoLastDownStation(newSection);
            return;
        }
        addSectionBetweenSectionsBasedOnUpStation(newSection);
    }


    private boolean doesStationExist(Station otherStation) {
        return getStations().stream().anyMatch(station -> station.equals(otherStation));
    }

    private void validateAddingSectionOrElseThrow(Section newSection, boolean doesUpStationExist, boolean doesDownStationExist) {
        if (!doesUpStationExist && !doesDownStationExist) {
            throw new StationsNotExistsException(
                    newSection.getUpStationName(), newSection.getDownStationName());
        }
        if (doesUpStationExist && doesDownStationExist) {
            throw new StationsExistException(
                    newSection.getUpStationName(), newSection.getDownStationName());
        }
    }

    private void addSectionOntoLastDownStation(Section newSection) {
        sections.add(newSection);
    }

    private void addSectionOntoLastUpStation(Section newSection) {
        sections.add(0, newSection);
    }

    private void addSectionBetweenSectionsBasedOnUpStation(Section newSection) {

        Section oldSection = sections.stream()
                .filter(section -> section.isUpStation(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        validateSectionDistanceOrElseThrow(newSection.getDistance(), oldSection.getDistance());

        int appendIndex = sections.indexOf(oldSection);
        sections.remove(appendIndex);
        sections.add(appendIndex, newSection);

        Distance oldSectionDistance = oldSection.getDistance().minus(newSection.getDistance());

        sections.add(
                appendIndex + 1,
                new Section(
                        oldSection.getLine(),
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSectionDistance));
    }

    private void validateSectionDistanceOrElseThrow(Distance newSectionDistance, Distance oldSectionDistance) {
        if (newSectionDistance.isLessThan(oldSectionDistance)) {
            return;
        }
        throw new InvalidSectionDistanceException(newSectionDistance, oldSectionDistance);
    }

    private void addSectionBetweenSectionsBasedOnDownStation(Section newSection) {
        Section oldSection = sections.stream()
                .filter(section -> section.isDownStation(newSection.getDownStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        validateSectionDistanceOrElseThrow(newSection.getDistance(), oldSection.getDistance());

        int appendIndex = sections.indexOf(oldSection);
        Distance oldSectionDistance = oldSection.getDistance().minus(newSection.getDistance());

        sections.remove(appendIndex);
        sections.add(appendIndex, newSection);
        sections.add(
                appendIndex,
                new Section(
                        oldSection.getLine(),
                        oldSection.getUpStation(),
                        newSection.getUpStation(),
                        oldSectionDistance));
    }

    public Optional<Section> findSectionWithUpStation(Station downStation) {
        return sections
                .stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findAny();
    }

    public Section findLastUpSection() {
        List<Station> downStations = findAllDownStations();

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }

    public Section findLastDownSection() {
        List<Station> upStations = findAllUpStations();

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }

    private List<Station> findAllUpStations() {
        return sections
                .stream()
                .map(Section::getUpStation)
                .collect(toList());
    }

    private List<Station> findAllDownStations() {
        return sections
                .stream()
                .map(Section::getDownStation)
                .collect(toList());
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int getSize() {
        return sections.size();
    }

    public void remove(Station station) {
        validateMoreThanOneSectionOrElseThrow();
        validateStationExistsOrElseThrow(station);

        Optional<Section> toRemoveUpStationSection = findUpStationSection(station);
        Optional<Section> toRemoveDownStationSection = findDownStationSection(station);

        toRemoveUpStationSection.ifPresent(section -> sections.remove(section));
        toRemoveDownStationSection.ifPresent(section -> sections.remove(section));

        if (toRemoveUpStationSection.isPresent() && toRemoveDownStationSection.isPresent()) {
            connectTwoSections(toRemoveUpStationSection.get(), toRemoveDownStationSection.get());
        }
    }

    private void connectTwoSections(Section upStationSection, Section downStationSection) {
        final Section newSection = new Section(
                upStationSection.getLine(),
                downStationSection.getUpStation(),
                upStationSection.getDownStation(),
                upStationSection.getDistance().plus(downStationSection.getDistance())
        );
        sections.add(newSection);
    }

    private Optional<Section> findUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny();
    }

    private Optional<Section> findDownStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny();
    }

    private void validateMoreThanOneSectionOrElseThrow() {
        if (sections.size() == SECTION_SIZE_LIMIT) {
            throw new InvalidSectionSizeLimitExecption(SECTION_SIZE_LIMIT);
        }
    }

    private void validateStationExistsOrElseThrow(Station otherStation) {
        boolean isStationExists = getStations().stream().anyMatch(station -> station.equals(otherStation));
        if (!isStationExists) {
            throw InvalidSectionDeleteException.ofNotExistStation(otherStation.getName());
        }
    }

    public boolean isLastDownStation(Station station) {
        return findLastDownSection().getDownStation().equals(station);
    }

    private boolean isLastUpStation(Station station) {
        return findLastUpSection().isUpStation(station);
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public List<Station> getStations() {
        List<Station> allStations = new ArrayList<>();
        Section lastUpSection = findLastUpSection();
        allStations.add(lastUpSection.getUpStation());

        Optional<Section> nextSection = Optional.ofNullable(lastUpSection);
        while (nextSection.isPresent()) {
            Section section = nextSection.get();
            allStations.add(section.getDownStation());
            nextSection = findSectionWithUpStation(section.getDownStation());
        }
        return allStations;
    }
}
