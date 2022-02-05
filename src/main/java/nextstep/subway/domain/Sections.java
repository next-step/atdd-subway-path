package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final boolean upStationExistence = isStationExistenceInStations(it -> it.isSameName(upStation));
        final boolean downStationExistence = isStationExistenceInStations(it -> it.isSameName(downStation));
        if ((upStationExistence && downStationExistence) || (!upStationExistence && !downStationExistence)) {
            throw new IllegalArgumentException("station is not valid");
        }
        if (upStationExistence) {
            updateSection(
                    it -> it.getUpStation().isSameName(upStation),
                    it -> it.updateUpStation(downStation, it.subtractDistance(section))
            );
        }
        if (downStationExistence) {
            updateSection(
                    it -> it.getDownStation().isSameName(downStation),
                    it -> it.updateDownStation(upStation, it.subtractDistance(section))
            );
        }
        sections.add(section);
    }

    private boolean isStationExistenceInStations(final Predicate<Station> predicate) {
        return getStations().stream().anyMatch(predicate);
    }

    private void updateSection(final Predicate<Section> predicate, final Consumer<Section> consumer) {
        sections.stream()
                .filter(predicate)
                .findAny()
                .ifPresent(consumer);
    }

    public void removeSection(final Station station) {
        if (sections.size() < REMOVABLE_CONDITION) {
            throw new IllegalStateException("sections is not removable state");
        }
        final boolean upStationExistence = isStationExistenceInSections(it -> it.isUpStation(station));
        final boolean downStationExistence = isStationExistenceInSections(it -> it.isDownStation(station));
        if (!upStationExistence && !downStationExistence) {
            throw new IllegalArgumentException("removed station is not include sections");
        }
        if (upStationExistence && downStationExistence) {
            final Section upStationSection = findSection(it -> it.isUpStation(station));
            final Section downStationSection = findSection(it -> it.isDownStation(station));
            downStationSection.updateDownStation(upStationSection.getDownStation(), upStationSection.addDistance(downStationSection));
            sections.remove(upStationSection);
            return;
        }
        if (upStationExistence) {
            sections.remove(findSection(it -> it.isUpStation(station)));
        }
        if (downStationExistence) {
            sections.remove(findSection(it -> it.isDownStation(station)));
        }
    }

    private boolean isStationExistenceInSections(final Predicate<Section> predicate) {
        return sections.stream().anyMatch(predicate);
    }

    private Section findSection(final Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Station> stations = new ArrayList<>();
        final Station upTerminalStation = findUpTerminalStation();
        stations.add(upTerminalStation);
        Optional<Station> nextStationOptional = nextStation(upTerminalStation);
        while (nextStationOptional.isPresent()) {
            final Station nextStation = nextStationOptional.get();
            stations.add(nextStation);
            nextStationOptional = nextStation(nextStation);
        }
        return Collections.unmodifiableList(stations);
    }

    private Station findUpTerminalStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> sections.stream().noneMatch(it -> it.isDownStation(upStation)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no have end upStation"));
    }

    private Optional<Station> nextStation(final Station station) {
        return sections.stream()
                .filter(it -> it.isUpStation(station))
                .map(Section::getDownStation)
                .findAny();
    }
}
