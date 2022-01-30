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
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        // 역 사이에 새로운 역을 등록할 경우
        if (hasSameUpStation(newSection)) {
            addSectionBetweenSectionsBasedOnUpStation(newSection);
            return;
        }
        if (hasSameDownStation(newSection)) {
            addSectionBetweenSectionsBasedOnDownStation(newSection);
            return;
        }
        // 새로운 역을 상행 종점으로 등록할 경우
        if (isLastUpStation(newSection.getDownStation())) {
            addSectionOntoLastUpStation(newSection);
            return;
        }
        // 새로운 역을 하행 종점으로 등록할 경우
        if (isLastDownStation(newSection.getUpStation())) {
            addSectionOntoLastDownStation(newSection);
            return;
        }
    }

    private void addSectionOntoLastDownStation(Section newSection) {
        sections.add(sections.size() - 1, newSection);
    }

    private void addSectionOntoLastUpStation(Section newSection) {
        sections.add(0, newSection);
    }

    private void addSectionBetweenSectionsBasedOnUpStation(Section newSection) {
        Section oldSection = sections.stream()
                .filter(section -> section.isUpStation(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        int appendIndex = sections.indexOf(oldSection);
        sections.remove(appendIndex);
        sections.add(appendIndex, newSection);
        sections.add(
                appendIndex + 1,
                new Section(
                        oldSection.getLine(),
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance() - newSection.getDistance()));
    }

    private void addSectionBetweenSectionsBasedOnDownStation(Section newSection) {
        Section oldSection = sections.stream()
                .filter(section -> section.isDownStation(newSection.getDownStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        int appendIndex = sections.indexOf(oldSection);
        sections.remove(appendIndex);
        sections.add(appendIndex, newSection);
        sections.add(
                appendIndex,
                new Section(
                        oldSection.getLine(),
                        oldSection.getUpStation(),
                        newSection.getUpStation(),
                        oldSection.getDistance() - newSection.getDistance()));
    }

    private boolean hasSameDownStation(Section otherSection) {
        Station otherDownStation = otherSection.getDownStation();

        return sections.stream()
                .map(Section::getDownStation)
                .anyMatch(station -> station.equals(otherDownStation));
    }

    private boolean hasSameUpStation(Section otherSection) {
        Station otherUpStation = otherSection.getUpStation();

        return sections.stream()
                .map(Section::getUpStation)
                .anyMatch(station -> station.equals(otherUpStation));
    }

    public Optional<Section> findSectionWithUpStation(Station downStation) {
        return sections
                .stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findAny();
    }

    public Section findLastUpSection() {
        List<Station> downStations = findAllDownStations();

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }

    public Section findLastDownSection() {
        List<Station> upStations = findAllUpStations();

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }

    private List<Station> findAllUpStations() {
        return sections
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findAllDownStations() {
        return sections
                .stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int getSize() {
        return sections.size();
    }

    public void remove(Station station) {
        Section toRemoveSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny()
                .orElseThrow(RuntimeException::new);

        sections.remove(toRemoveSection);
    }

    public boolean isLastDownStation(Station station) {
        return findLastDownSection().getDownStation().equals(station);
    }

    private boolean isLastUpStation(Station station) {
        return findLastUpSection().isUpStation(station);
    }

    public Section get(int index) {
        return sections.get(index);
    }
}
