package nextstep.subway.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return getStations().stream()
            .limit(size())
            .map(this::getFirstSectionHavingSameUpStation)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (isDuplicatedSection(section)) {
            throw new IllegalArgumentException("Duplicated section");
        }

        if (isNotConnectable(section)) {
            throw new IllegalArgumentException("Cannot add section consisting of non-existing stations");
        }

        addAndRearrange(section);
    }


    public void removeStation(Station station) {
        if (isNotRemovable(station)) {
            throw new IllegalStateException("Cannot Remove Station");
        }

        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> result = new ArrayList<>(size() + 1);

        Station upStation = getFirstStation();
        result.add(upStation);
        for (int i = 0; i < size(); i++) {
            Optional<Section> section = getFirstSectionHavingSameUpStation(upStation);

            if (section.isEmpty()) {
                break;
            }

            upStation = section.get().getDownStation();
            result.add(upStation);
        }

        return result;
    }

    public Station getFirstStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return upStations.stream()
            .filter(it -> !downStations.contains(it))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    public Station getLastStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return downStations.stream()
            .filter(it -> !upStations.contains(it))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    private boolean isNotRemovable(Station station) {
        return !isRemovable(station);
    }

    private boolean isRemovable(Station station) {
        return getLastStation().equals(station);
    }

    private boolean isDuplicatedSection(Section section) {
        return sections.stream()
            .anyMatch(section::isSameAsSection);
    }

    private boolean isNotConnectable(Section section) {
        return !isConnectable(section);
    }

    private boolean isConnectable(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        return contains(upStation) || contains(downStation);
    }

    private boolean contains(Station station) {
        return getStations().contains(station);
    }

    private int size() {
        return sections.size();
    }

    private List<Station> getUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    private Optional<Section> getFirstSectionHavingSameUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.isSameAsUpStation(station))
            .findFirst();
    }

    private Optional<Section> getFirstSectionHavingSameDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.isSameAsDownStation(station))
            .findFirst();
    }

    private void addAndRearrange(Section section) {
        rearrangeInBaseOnUpStation(section);
        rearrangeInBaseOnDownStation(section);

        sections.add(section);
    }

    private void rearrangeInBaseOnUpStation(Section section) {
        getFirstSectionHavingSameUpStation(section.getUpStation())
            .ifPresent(oldSection -> {
                checkArgument(
                    section.getDistance() < oldSection.getDistance(),
                    "Section distance is too long"
                );

                Section rearrangeSection = new Section(
                    oldSection.getLine(),
                    section.getDownStation(),
                    oldSection.getDownStation(),
                    oldSection.getDistance() - section.getDistance()
                );

                sections.add(rearrangeSection);
                sections.remove(oldSection);
            });
    }

    private void rearrangeInBaseOnDownStation(Section section) {
        getFirstSectionHavingSameDownStation(section.getDownStation())
            .ifPresent(oldSection -> {
                checkArgument(
                    section.getDistance() < oldSection.getDistance(),
                    "Section distance is too long"
                );

                Section rearrangeSection = new Section(
                    oldSection.getLine(),
                    oldSection.getUpStation(),
                    section.getUpStation(),
                    oldSection.getDistance() - section.getDistance()
                );

                sections.add(rearrangeSection);
                sections.remove(oldSection);
            });
    }
}
