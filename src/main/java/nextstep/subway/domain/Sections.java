package nextstep.subway.domain;

import nextstep.subway.domain.exception.EntityAlreadyExistsException;
import nextstep.subway.domain.exception.EntityNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        validateSections(upStation, downStation);

        if (isEndSection(upStation, downStation)) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        if (checkedDistance(upStation, new Section(line, upStation, downStation, distance))) {
            throw new IllegalArgumentException("The length of the section you want to register is greater than or equal to the length of the existing section");
        }
        sections.add(new Section(line, upStation, downStation, distance));
    }

    private boolean checkedDistance(Station upStation, Section newSection) {
        Section oldSection = getSectionByUpStation(upStation);
        return newSection.getDistance() >= oldSection.getDistance();
    }

    private Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("%s Section is not found", upStation.getName())));
    }

    private boolean isEndSection(Station upStation, Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(downStation)) ||
                sections.stream()
                        .anyMatch(section -> section.getDownStation().equals(upStation));
    }

    private void validateSections(Station upStation, Station downStation) {
        if (isContainsStation(upStation) && isContainsStation(downStation)) {
            throw new EntityAlreadyExistsException("Both stations already exist on the line.");
        }

        if (!isContainsStation(upStation) && !isContainsStation(downStation)) {
            throw new EntityAlreadyExistsException("Either station should be included.");
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.stream().map(Section::getUpStation).findFirst().get());

        return stations;
    }

    public void removeSection(Station station) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("If there is less than one registered section, you cannot delete it.");
        }

        if (!getLastSection().isDownStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private boolean isContainsStation(Station station) {
        List<Station> stations = getStations();
        return stations.contains(station);
    }

    public int getDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance).sum();
    }
}
