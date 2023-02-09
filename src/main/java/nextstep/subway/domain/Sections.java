package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
@Getter
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(final Line line, final Station upStation, final Station downStation, final int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        if (sections.containsAll(List.of(upStation, downStation)) ||
                (!sections.contains(upStation) && !sections.contains(downStation))) {
            throw new IllegalArgumentException();
        }

        final Station lastUpStation = sections.get(0).getUpStation();
        final Station lastDownStation = sections.get(sections.size() - 1).getDownStation();
        if (lastDownStation.equals(upStation)) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        if (lastUpStation.equals(downStation)) {
            sections.add(0, new Section(line, upStation, downStation, distance));
            return;
        }
        sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .ifPresent(it -> {
                    if (distance >= it.getDistance()) {
                        throw new IllegalArgumentException();
                    }
                    sections.add(new Section(line, upStation, downStation, distance));
                    sections.add(new Section(line, downStation, it.getDownStation(), it.getDistance() - distance));
                    sections.remove(it);
                });
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        final List<Station> stations = new ArrayList<>();
        final Section lastUpSection = getLastUpSection(sections.stream().findFirst().get());
        stations.add(lastUpSection.getUpStation());
        stations.add(lastUpSection.getDownStation());

        Section currentSection = lastUpSection;
        while (true) {
            final Section nextSection = getNextSection(currentSection);
            if (nextSection == null) break;
            stations.add(nextSection.getDownStation());
            currentSection = nextSection;
        }
        return stations;
    }

    public void remove(final Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
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

    private Section getNextSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return null;
        }
        return findSection.get();
    }
}
