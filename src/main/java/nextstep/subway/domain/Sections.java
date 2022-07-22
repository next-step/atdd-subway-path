package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
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
        validateBeforeAdd(section);
        if (isBetweenUpStationAndDownStation(section)) {
            addSectionBetweenUpStationAndDownStation(section);
            return;
        }
        sections.add(section);
    }

    private void validateBeforeAdd(Section section) {
        if (sections.isEmpty()) {
            return;
        }
        if (doesNotContainStation(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
        if (hasSameSection(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
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

    public void deleteSection(Station station) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나뿐인 노선에서 구간을 제거할 수 없습니다.");
        }
        if (doesNotContainStation(station)) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역은 제거 할 수 없습니다.");
        }
        if (isDeleteLastSection(station)) {
            deleteLastSection();
            return;
        }
        if (isDeleteFirstSection(station)) {
            deleteFirstSection();
            return;
        }
        deleteMiddleSection(station);
    }

    private boolean isDeleteLastSection(Station station) {
        return getLastSection().equalsDownStation(station);
    }

    private void deleteMiddleSection(Station station) {
        final Section target = sections.stream()
                .filter(section -> section.equalsDownStation(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        final int index = sections.indexOf(target);
        final Section nextSection = sections.get(index + 1);

        sections.removeAll(Arrays.asList(target, nextSection));
        sections.add(index, new Section(target.getLine(), target.getUpStation(), nextSection.getDownStation(), target.plusDistance(nextSection)));
    }

    private void deleteFirstSection() {
        sections.remove(findFirstSection());
    }

    private boolean isDeleteFirstSection(Station station) {
        final Section firstSection = findFirstSection();
        return firstSection.equalsUpStation(station);
    }

    private void deleteLastSection() {
        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return getOrderedSections().get(sections.size() - 1);
    }

    public int getDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private void addSectionBetweenUpStationAndDownStation(Section section) {
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

    private boolean doesNotContainStation(Station... stations) {
        final List<Station> allStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        allStations.add(getLastSection().getDownStation());

        final int sizeBeforeRemove = allStations.size();
        allStations.removeAll(Arrays.asList(stations));
        final int sizeAfterRemove = allStations.size();

        return sizeBeforeRemove == sizeAfterRemove;
    }

    private boolean hasSameSection(Section section) {
        return sections.stream().anyMatch(e -> e.equals(section));
    }

    public List<Section> getOrderedSections() {
        if (sections.isEmpty() || sections.size() == 1) {
            return sections;
        }
        final List<Section> orderedSections = new ArrayList<>();
        Section firstSection = findFirstSection();
        orderedSections.add(firstSection);
        addNextSection(orderedSections, firstSection);
        return orderedSections;
    }

    private void addNextSection(List<Section> orderedSections, Section beforeSection) {
        Station nextUpStation = beforeSection.getDownStation();
        final Optional<Section> optionalNextSection = sections.stream()
                .filter(section -> section.equalsUpStation(nextUpStation))
                .findAny();
        if (optionalNextSection.isPresent()) {
            final Section nextSection = optionalNextSection.get();
            orderedSections.add(nextSection);
            addNextSection(orderedSections, nextSection);
            return;
        }
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(section -> isUpStation(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isUpStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.equalsDownStation(station));
    }
}
