package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
        sections = new ArrayList<>();
    }

    public void add(Section section) {
        if (isBetweenUpStationAndDownStation(section)) {
            addInterSection(section);
            return;
        }
        sections.add(section);
    }

    private boolean isBetweenUpStationAndDownStation(Section section) {
        boolean matchInUpStations = matchInUpStations(section.getUpStation());
        boolean matchInDownStations = matchInDownStations(section.getDownStation());
        return matchInUpStations || matchInDownStations;
    }

    private boolean matchInDownStations(Station station) {
        return sections.stream().map(Section::getDownStation)
                .anyMatch(downStation -> downStation.equals(station));
    }

    private boolean matchInUpStations(Station station) {
        return sections.stream().map(Section::getUpStation)
                .anyMatch(upStation -> upStation.equals(station));
    }

    public void deleteSection(Section section) {
        sections.stream()
                .filter(e -> e.equals(section))
                .findFirst()
                .ifPresent(sections::remove);
    }

    public Section getLastSection() {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return sections.get(sections.size() - 1);
    }

    public int getDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    private void addInterSection(Section section) {
        if (matchInUpStations(section.getUpStation())) {
            addSectionByUpStation(section);
            return;
        }
        if (matchInDownStations(section.getDownStation())) {
            addSectionByDownStation(section);
            return;
        }
        throw new IllegalArgumentException();
    }

    private void addSectionByDownStation(Section section) {
        final Section oldSection = findSectionMatchDownStation(section);

        final int distance = getDistance(section, oldSection);
        final Line line = oldSection.getLine();
        final int index = sections.indexOf(oldSection);
        sections.remove(index);
        sections.add(index, new Section(line, oldSection.getUpStation(), section.getUpStation(), distance));
        sections.add(index + 1, new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    private Section findSectionMatchDownStation(Section section) {
        return sections.stream()
                .filter(e -> e.equalsDownStation(section.getDownStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void addSectionByUpStation(Section section) {
        final Section oldSection = findSectionMatchUpStation(section);

        final int distance = getDistance(section, oldSection);
        final Line line = oldSection.getLine();
        final int index = sections.indexOf(oldSection);
        sections.remove(index);
        sections.add(index, new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance()));
        sections.add(index + 1, new Section(line, section.getDownStation(), oldSection.getDownStation(), distance));
    }

    private Section findSectionMatchUpStation(Section section) {
        return sections.stream()
                .filter(e -> e.equalsUpStation(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private int getDistance(Section section, Section oldSection) {
        final int distance = oldSection.minusDistance(section);
        if (distance <= 0) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
        return distance;
    }

    public boolean doesNotContainStationOf(Section section) {
        final List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getLastSection().getDownStation());

        final int sizeBeforeRemove = stations.size();
        stations.remove(section.getUpStation());
        stations.remove(section.getDownStation());
        final int sizeAfterRemove = stations.size();

        return sizeBeforeRemove == sizeAfterRemove;
    }

    public boolean hasSameSection(Section section) {
        return sections.stream().anyMatch(e -> e.equals(section));
    }

    public List<Section> getOrderedSections() {
        if (sections.isEmpty() || sections.size() == 1) {
            return sections;
        }

        final List<Section> orderedSections = new ArrayList<>();
        Section firstSection = findFirstSection(sections.get(0));
        orderedSections.add(firstSection);
        addNextSection(orderedSections, firstSection);
        return orderedSections;
    }

    private void addNextSection(List<Section> orderedSections, Section beforeSection) {
        Station nextUpStation = beforeSection.getDownStation();
        final Optional<Section> optionalNextSection = sections.stream()
                .filter(section -> section.getUpStation().equals(nextUpStation))
                .findAny();
        if (optionalNextSection.isPresent()) {
            final Section nextSection = optionalNextSection.get();
            orderedSections.add(nextSection);
            addNextSection(orderedSections, nextSection);
            return;
        }
    }

    private Section findFirstSection(final Section firstSection) {
        final Optional<Section> optionalSection = sections.stream()
                .filter(section -> section.equalsDownStation(firstSection.getUpStation()))
                .findAny();
        if (optionalSection.isPresent()) {
            return findFirstSection(optionalSection.get());
        }
        return firstSection;
    }
}
