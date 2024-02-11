package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        validateSectionToAdd(section);

        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (isLastStation(section.getUpStation())) {
            sections.add(section);
            return;
        }

        if (isFirstStation(section.getDownStation())) {
            sections.add(0, section);
            return;
        }

        addInTheMiddle(section);
    }

    public void remove(Station upStation, Station downStation) {
        sections = sections.stream()
            .filter(isNotTheSectionToDelete(upStation, downStation))
            .collect(Collectors.toList());
    }

    public void removeStation(Station station) {
        if (isFirstStation(station)) {
            remove(getFirstSection());
            return;
        }

        if(isLastStation(station)) {
            remove(getLastSection());
            return;
        }

        removeStationInTheMiddle(station);
    }


    public List<Station> getStations() {
        final Set<Station> stationSet = new HashSet<>();
        sections.forEach(section -> {
            stationSet.add(section.getUpStation());
            stationSet.add(section.getDownStation());
        });

        return List.copyOf(stationSet);
    }

    private void removeStationInTheMiddle(Station station) {
        final Section upSection = findSectionByUpStation(station);
        final Section downSection = findSectionByDownStation(station);

        upSection.extend(downSection);
        remove(downSection);
    }

    private void addInTheMiddle(Section section) {
        final Section upSection = findSectionByUpStation(section.getUpStation());

        upSection.shorten(section);
        sections.add(sections.indexOf(upSection), section);
    }

    private Section findSectionByDownStation(Station downStation) {
        return sections.stream()
            .filter(section -> section.getDownStation() == downStation)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findSectionByUpStation(Station upStation) {
        return sections.stream()
            .filter(section -> section.getUpStation() == upStation)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isLastStation(Station station) {
        return Objects.equals(getLastSection().getDownStation(), station);
    }

    private boolean isFirstStation(Station station) {
        return Objects.equals(getFirstSection().getUpStation(), station);
    }

    private Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("no section found");
        }

        return sections.get(0);
    }

    private Section getLastSection() {
        if(sections.isEmpty()) {
            throw new IllegalArgumentException("no section found");
        }

        return sections.get(sections.size() - 1);
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    private void validateSectionToAdd(Section sectionToAdd) {
        final Set<Station> stationSet = new HashSet<>(getStations());

        if(stationSet.containsAll(List.of(sectionToAdd.getUpStation(), sectionToAdd.getDownStation()))) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if(!stationSet.isEmpty() && !stationSet.contains(sectionToAdd.getUpStation()) && !stationSet.contains(sectionToAdd.getDownStation())) {
            throw new IllegalArgumentException("연결할 수 없는 구간입니다.");
        }
    }
    private Predicate<Section> isNotTheSectionToDelete(Station upStation, Station downStation) {
        return section ->
            section.getUpStation() != upStation
                && section.getDownStation() != downStation;
    }


}
