package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    private static final int REMOVABLE_CONDITION = 2;

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
        final boolean upStationExistence = getStations().stream().anyMatch(it -> it.isSameName(section.getUpStation()));
        final boolean downStationExistence = getStations().stream().anyMatch(it -> it.isSameName(section.getDownStation()));
        if ((upStationExistence && downStationExistence) || (!upStationExistence && !downStationExistence)) {
            throw new IllegalArgumentException("station is not valid");
        }
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        if (upStationExistence) {
            sections.stream()
                    .filter(it -> it.getUpStation().isSameName(upStation))
                    .findFirst()
                    .ifPresent(it -> it.updateUpStation(downStation, it.subtractDistance(section)));
        }
        if (downStationExistence) {
            sections.stream()
                    .filter(it -> it.getDownStation().isSameName(downStation))
                    .findAny()
                    .ifPresent(it -> it.updateDownStation(upStation, it.subtractDistance(section)));
        }
        sections.add(section);
    }

    public void removeSection(final Station station) {
        if (sections.size() < REMOVABLE_CONDITION) {
            throw new IllegalStateException("sections is not removable state");
        }
        final boolean up = sections.stream().anyMatch(it -> it.isUpStation(station));
        final boolean down = sections.stream().anyMatch(it -> it.isDownStation(station));
        if (up == false && down == false) {
            throw new IllegalArgumentException("removed station is not include sections");
        }
        if (up == true && down == true) {
            final Section upStationSection = sections.stream().filter(it -> it.isUpStation(station)).findAny().get();
            final Section downStationSection = sections.stream().filter(it -> it.isDownStation(station)).findAny().get();
            downStationSection.updateDownStation(upStationSection.getDownStation(), upStationSection.getDistance() + downStationSection.getDistance());
            this.sections.remove(upStationSection);
            return;
        }
        if (up) {
            final Section section = sections.stream().filter(it -> it.isUpStation(station)).findAny().get();
            this.sections.remove(section);
        }
        if (down) {
            final Section section = sections.stream().filter(it -> it.isDownStation(station)).findAny().get();
            this.sections.remove(section);
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Station> stations = new ArrayList<>();
        final Station upTerminalStation = sections.stream()
                .map(Section::getUpStation)
                .filter(it -> sections.stream().noneMatch(it2 -> it2.isDownStation(it)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no have end upStation"));

        stations.add(upTerminalStation);
        Optional<Station> nextStationOptional = sections.stream()
                .filter(it -> it.isUpStation(upTerminalStation))
                .map(Section::getDownStation)
                .findAny();
        while (nextStationOptional.isPresent()) {
            final Station nextStation = nextStationOptional.get();
            stations.add(nextStation);
            nextStationOptional = sections.stream()
                    .filter(it -> it.isUpStation(nextStation))
                    .map(Section::getDownStation)
                    .findAny();
        }
        return Collections.unmodifiableList(stations);
    }
}
