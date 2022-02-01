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

    private static final int GAP_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        final boolean upStationExistence = isUpStationStation(section);
        final boolean downStationExistence = isDownStationStation(section);
        if ((upStationExistence && downStationExistence) || (!upStationExistence && !downStationExistence)) {
            throw new IllegalArgumentException("station is not valid");
        }
        if (upStationExistence) {
            updateUpStation(section);
        }
        if (downStationExistence) {
            updateDownStation(section);
        }
        sections.add(section); // 강남 논현
    }

    private boolean isUpStationStation(final Section section) {
        return sections.stream()
                .map(Section::getUpStation)
                .anyMatch(isSameStationName(section));
    }

    private boolean isDownStationStation(final Section section) {
        return sections.stream()
                .map(Section::getDownStation)
                .anyMatch(isSameStationName(section));
    }

    private Predicate<Station> isSameStationName(final Section section) {
        return it -> it.isSameName(section.getUpStation()) || it.isSameName(section.getDownStation());
    }

    private void updateUpStation(final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final Section alreadySection = findUpStationSection(upStation, downStation);
        if (alreadySection.isUpStation(upStation)) {
            final int updateDistance = alreadySection.getDistance() - section.getDistance();
            alreadySection.updateUpStation(section.getDownStation(), updateDistance);
        }
    }

    private Section findUpStationSection(final Station upStation, final Station downStation) {
        return sections.stream().filter(it -> it.isUpStation(upStation) || it.isUpStation(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("station is not found"));
    }

    private void updateDownStation(final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final Section alreadySection = findDownStationSection(upStation, downStation);
        if (alreadySection.isDownStation(downStation)) {
            final int updateDistance = alreadySection.getDistance() - section.getDistance();
            alreadySection.updateDownStation(section.getUpStation(), updateDistance);
        }
    }

    private Section findDownStationSection(final Station upStation, final Station downStation) {
        return sections.stream().filter(it -> it.isDownStation(upStation) || it.isDownStation(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("station is not found"));
    }

    public void removeSection(final Station station) {
        validateRemoveSection(station);
        this.sections.remove(lastIndex());
    }

    private void validateRemoveSection(final Station station) {
        if (!sections.get(sections.size() - GAP_SIZE).getDownStation().equals(station)) {
            throw new IllegalArgumentException("invalid remove section");
        }
    }

    private int lastIndex() {
        return sections.size() - GAP_SIZE;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Station> stations = new ArrayList<>();
        final Station endUpStation = findEndUpStation();
        stations.add(endUpStation);
        Optional<Station> nextSection = findNextSection(endUpStation);
        while (nextSection.isPresent()) {
            final Station downStation = nextSection.get();
            stations.add(downStation);
            nextSection = findNextSection(downStation);
        }
        return Collections.unmodifiableList(stations);
    }

    private Station findEndUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isNotDownStation)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no have end upStation"));
    }

    private boolean isNotDownStation(final Station station) {
        return sections.stream()
                .noneMatch(it -> it.isDownStation(station));
    }

    private Optional<Station> findNextSection(final Station station) {
        return sections.stream()
                .filter(it -> it.isUpStation(station))
                .map(Section::getDownStation)
                .findAny();
    }
}
