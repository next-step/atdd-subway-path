package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Line line, final Station upStation, final Station downStation, final int distance) {
        if (this.sections.size() == 0) {
            this.sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        validateAddSection(upStation, downStation);

        if (isUpStationEndpoint(downStation) || isDownStationEndpoint(upStation)) {
            this.sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        Section sectionByUpStation = getSectionByUpStation(upStation);
        if (Objects.nonNull(sectionByUpStation)) {
            addBetweenSection(sectionByUpStation, sectionByUpStation.getUpStation(), downStation, sectionByUpStation.getDownStation(), distance, sectionByUpStation.getDistance() - distance);
            return;
        }

        Section sectionByDownStation = getSectionByDownStation(downStation);
        if (Objects.nonNull(sectionByDownStation)) {
            addBetweenSection(sectionByDownStation, sectionByDownStation.getUpStation(), upStation, downStation, sectionByDownStation.getDistance() - distance, distance);
        }
    }

    public void deleteSection(final Station station) {
        validateDeleteSection(station);

        if (isUpStationEndpoint(station)) {
            this.sections.remove(getSectionByUpStation(station));
            return;
        }

        if (isDownStationEndpoint(station)) {
            this.sections.remove(getSectionByDownStation(station));
            return;
        }

        if (!(isUpStationEndpoint(station) || isDownStationEndpoint(station))) {
            Sections sectionsConnectedByStation = sectionsConnectedByStation(station);
            Section firstSection = sectionsConnectedByStation.getFirstSection();
            Section secondSection = sectionsConnectedByStation.getSecondSection();

            this.sections.remove(firstSection);
            this.sections.remove(secondSection);
            this.sections.add(new Section(firstSection.getLine(), firstSection.getUpStation(), secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance()));
        }
    }

    public List<Section> getSortedSections() {
        return this.sort().sections;
    }

    private void validateAddSection(final Station upStation, final Station downStation) {
        List<Station> allStations = allStations();

        if (bothStationsAreAlreadyInExistingStations(upStation, downStation, allStations)
                || nonStationsAreInExistingStations(upStation, downStation, allStations)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateDeleteSection(final Station station) {
        if (this.sections.size() <= MINIMUM_SECTION_SIZE || noExistsStation(station)) {
            throw new IllegalArgumentException();
        }
    }

    private Section getFirstSection() {
        return this.sort().sections.get(0);
    }

    private Section getSecondSection() {
        return this.sort().sections.get(1);
    }

    private boolean nonStationsAreInExistingStations(final Station upStation, final Station downStation, final List<Station> allStations) {
        return !(allStations.contains(upStation) || allStations.contains(downStation));
    }

    private boolean bothStationsAreAlreadyInExistingStations(final Station upStation, final Station downStation, final List<Station> allStations) {
        return allStations.contains(upStation) && allStations.contains(downStation);
    }

    private boolean noExistsStation(final Station station) {
        return !allStations().contains(station);
    }

    private void addBetweenSection(Section section, Station upStation, Station middleStation, Station downStation, int distanceBetweenUpAndMiddleStation, int distanceBetweenMiddleAndDownStation) {
        if (distanceBetweenUpAndMiddleStation <= 0 || distanceBetweenMiddleAndDownStation <= 0) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(section);
        this.sections.add(new Section(section.getLine(), upStation, middleStation, distanceBetweenUpAndMiddleStation));
        this.sections.add(new Section(section.getLine(), middleStation, downStation, distanceBetweenMiddleAndDownStation));
    }

    public boolean isDownStationEndpoint(Station station) {
        return getDownStationEndpoint().equals(station);
    }

    private boolean isUpStationEndpoint(Station station) {
        return getUpStationEndpoint().equals(station);
    }

    private Station getUpStationEndpoint() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return upStations.stream()
                .filter(it -> !downStations.contains(it))
                .findAny()
                .orElse(null);
    }

    private Station getDownStationEndpoint() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return downStations.stream()
                .filter(it -> !upStations.contains(it))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Section getSectionByUpStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findAny()
                .orElse(null);
    }

    private Section getSectionByDownStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findAny()
                .orElse(null);
    }

    private Sections sectionsConnectedByStation(Station station) {
        return new Sections(this.sections.stream()
                .filter(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))
                .collect(Collectors.toList()));
    }

    private Sections sort() {
        List<Section> sections = new ArrayList<>();
        Station upStation = this.getUpStationEndpoint();
        if (Objects.isNull(upStation)) {
            return new Sections(sections);
        }

        do {
            Section section = this.getSectionByUpStation(upStation);
            sections.add(section);
            upStation = section.getDownStation();
        } while (!this.isDownStationEndpoint(upStation));

        this.sections = sections;
        return this;
    }

    private List<Station> allStations() {
        List<Station> allStations = new ArrayList<>();

        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        Station downStation = getDownStationEndpoint();

        allStations.addAll(upStations);
        allStations.add(downStation);

        return allStations;
    }
}
