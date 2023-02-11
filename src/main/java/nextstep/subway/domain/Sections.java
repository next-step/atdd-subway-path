package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import nextstep.subway.domain.exception.NotExistBasedOnDownStationException;
import nextstep.subway.domain.exception.NotExistBasedOnUpStationException;
import nextstep.subway.domain.exception.RemoveSectionsSizeException;
import nextstep.subway.domain.exception.SectionsEmptyException;
import nextstep.subway.domain.exception.StationNotInSectionsException;

@Embeddable
public class Sections {
    private static final int REMOVE_SIZE_MIN = 2;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Transient
    private final SectionsValidator sectionsValidator;

    public Sections() {
        this.sectionsValidator = new SectionsValidator();
    }

    public void add(final Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }
        List<Section> sectionsBasedOnUpStation = getSectionsBy(section.getUpStation());
        List<Section> sectionsBasedOnDownStation = getSectionsBy(section.getDownStation());
        sectionsValidator.addSection(sections, sectionsBasedOnUpStation, sectionsBasedOnDownStation);
        if (sectionsBasedOnUpStation.isEmpty()) {
            addBasedOnDownStationSection(section);
        }
        if (sectionsBasedOnDownStation.isEmpty()) {
            addBasedOnUpStationSection(section);
        }
    }

    private void addBasedOnDownStationSection(final Section section) {
        if (section.getDownStation().equals(getLineUpStation())) {
            sections.add(section);
            return;
        }
        Section existingSection = findSectionBasedOnDownStationBy(section.getDownStation());
        sectionsValidator.distanceAddSectionBetweenExistingSection(section, existingSection);
        existingSection
                .setDownStation(section.getUpStation())
                .minusDistance(section.getDistance());
        sections.add(section);
    }

    private void addBasedOnUpStationSection(final Section section) {
        if (section.getUpStation().equals(getLineDownStation())) {
            sections.add(section);
            return;
        }
        Section existingSection = findSectionBasedOnUpStationBy(section.getUpStation());
        sectionsValidator.distanceAddSectionBetweenExistingSection(section, existingSection);
        existingSection
                .setUpStation(section.getDownStation())
                .minusDistance(section.getDistance());
        sections.add(section);
    }

    public void remove(final Station station) {
        validateRemoveBy(station);

        if (isLineUpStation(station)) {
            sections.remove(0);
            return;
        }
        if (isLineDownStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        Section sectionToRemoveDownStation = findSectionBasedOnDownStationBy(station);
        Section sectionToRemoveUpStation = findSectionBasedOnUpStationBy(station);
        sections.removeAll(List.of(sectionToRemoveDownStation, sectionToRemoveUpStation));

        Section relocateSection
                = relocateWithRemovingMiddleSection(sectionToRemoveDownStation, sectionToRemoveUpStation);
        sections.add(relocateSection);
    }

    private void validateRemoveBy(final Station station) {
        if (sections.size() < REMOVE_SIZE_MIN) {
            throw new RemoveSectionsSizeException(REMOVE_SIZE_MIN);
        }
        if (!isContain(station)) {
            throw new StationNotInSectionsException();
        }
    }

    private boolean isContain(final Station station) {
        return sections.stream().anyMatch(section -> section.isContain(station));
    }

    private Section findSectionBasedOnDownStationBy(final Station downStation) {
        return getSectionsBy(downStation).stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst()
                .orElseThrow(NotExistBasedOnDownStationException::new);
    }

    private Section relocateWithRemovingMiddleSection(
            final Section sectionToRemoveDownStation,
            final Section sectionToRemoveUpStation
    ) {
        Line line = sectionToRemoveUpStation.getLine();
        Station upStation = sectionToRemoveDownStation.getUpStation();
        Station downStation = sectionToRemoveUpStation.getDownStation();
        Distance distance = sectionToRemoveDownStation.getDistance().plus(sectionToRemoveUpStation.getDistance());
        return new Section(line, upStation, downStation, distance);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(getSortedStation());
    }

    private List<Station> getSortedStation() {
        List<Station> stations = new ArrayList<>();
        Station upStation = getLineUpStation();
        Station lineDownStation = getLineDownStation();
        while (upStation != lineDownStation) {
            Section upStationSection = findSectionBasedOnUpStationBy(upStation);
            stations.add(upStationSection.getUpStation());
            upStation = upStationSection.getDownStation();
        }
        stations.add(lineDownStation);
        return stations;
    }

    private Section findSectionBasedOnUpStationBy(final Station station) {
        return getSectionsBy(station).stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseThrow(NotExistBasedOnUpStationException::new);
    }

    private List<Section> getSectionsBy(final Station station) {
        return sections.stream()
                .filter(s -> s.isContain(station))
                .collect(Collectors.toUnmodifiableList());
    }

    public Station getLineDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(this::isLineDownStation)
                .findFirst()
                .orElseThrow(SectionsEmptyException::new);
    }

    private boolean isLineDownStation(final Station station) {
        return findContainSectionsBy(station).stream()
                .filter(section -> section.isUpStation(station))
                .findFirst().isEmpty();
    }

    public Station getLineUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isLineUpStation)
                .findFirst()
                .orElseThrow(SectionsEmptyException::new);
    }

    private boolean isLineUpStation(final Station station) {
        return findContainSectionsBy(station).stream()
                .filter(section -> section.isDownStation(station))
                .findFirst().isEmpty();
    }

    private List<Section> findContainSectionsBy(final Station station) {
        List<Section> result = sections.stream()
                .filter(section -> section.isContain(station))
                .collect(Collectors.toUnmodifiableList());
        if (result.isEmpty()) {
            throw new StationNotInSectionsException();
        }
        return result;
    }
}
