package nextstep.subway.domain;

import lombok.Getter;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
@Getter
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    void add(final Line line, final Station upStation, final Station downStation, final int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        validateUpStationAndDownStation(upStation, downStation);

        if (isAddSectionWithLastUpStationOrLastDownStation(upStation, downStation)) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        addStationBetweenStations(line, upStation, downStation, distance);
    }

    List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        final List<Station> stations = new ArrayList<>();
        final Section lastUpSection = getLastUpSection();
        stations.add(lastUpSection.getUpStation());

        Section currentSection = lastUpSection;
        while (true) {
            stations.add(currentSection.getDownStation());
            final Section nextSection = getNextSection(currentSection);
            if (nextSection == null) break;
            currentSection = nextSection;
        }
        return stations;
    }

    void remove(final Station station) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException();
        }
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException();
        }
        final List<Section> sections = this.sections.stream()
                .filter(section -> section.getUpStation().equals(station) ||
                        section.getDownStation().equals(station))
                .collect(Collectors.toList());
        if (sections.size() > 1) {
            final Section firstSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(station))
                    .findFirst().get();
            final Section secondSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(station))
                    .findFirst().get();
            this.sections.remove(firstSection);
            this.sections.remove(secondSection);
            this.sections.add(new Section(firstSection.getLine(), firstSection.getUpStation(), secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance()));
            return;
        }
        this.sections.remove(getSection(station));
    }

    private void validateUpStationAndDownStation(final Station upStation, final Station downStation) {
        if (new HashSet<>(this.getStations()).containsAll(List.of(upStation, downStation)) ||
                (!this.getStations().contains(upStation) && !this.getStations().contains(downStation))) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isAddSectionWithLastUpStationOrLastDownStation(final Station upStation, final Station downStation) {
        final Station lastUpStation = getLastUpSection().getUpStation();
        final Station lastDownStation = getLastDownSection().getDownStation();
        return lastDownStation.equals(upStation) || lastUpStation.equals(downStation);
    }

    private void addStationBetweenStations(final Line line, final Station upStation, final Station downStation, final int distance) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .ifPresent(it -> {
                    validateNewSectionDistance(it, distance);
                    sections.add(new Section(line, upStation, downStation, distance));
                    sections.add(new Section(line, downStation, it.getDownStation(), it.getDistance() - distance));
                    sections.remove(it);
                });
    }

    private void validateNewSectionDistance(final Section section, final int distance) {
        if (section.getDistance() <= distance) {
            throw new IllegalArgumentException();
        }
    }

    private Section getLastUpSection() {
        return getLastUpSection(this.sections.get(0));
    }

    private Section getLastUpSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return section;
        }
        return getLastUpSection(findSection.get());
    }

    private Section getLastDownSection() {
        return getLastDownSection(this.sections.get(0));
    }

    private Section getLastDownSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return section;
        }
        return getLastDownSection(findSection.get());
    }

    private Section getNextSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return null;
        }
        return findSection.get();
    }

    private Section getSection(final Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(station) ||
                        section.getDownStation().equals(station))
                .findFirst().get();
    }
}
