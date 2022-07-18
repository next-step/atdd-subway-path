package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    private final int MINIMUM_SECTIONS_SIZE = 0;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section addedSection) {
        if (sections.size() > MINIMUM_SECTIONS_SIZE) {
            isContains(addedSection);
            Section matchedSection = getMatchedSection(addedSection);
            changeMatchStation(addedSection, matchedSection);
            changeAroundStationByMatchStation(addedSection, matchedSection);
        }
        sections.add(addedSection);
    }

    private void isContains(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException("같은 구간이 있습니다.");
        }
    }

    private Section getMatchedSection(Section section) {
        Optional<Section> matchSection = sections.stream()
                .filter(section::anyMatch)
                .findFirst();

        if (matchSection.isEmpty()) {
            throw new IllegalArgumentException("상행역 하행역이 모두 포함되어있지 않습니다");
        }

        return matchSection.get();
    }

    private void changeMatchStation(Section addedSection, Section matchedSection) {
        if (matchedSection.getUpStation().equals(addedSection.getUpStation())) {
            matchedSection.changeUpStation(addedSection.getDownStation());
            matchedSection.changeDistance(addedSection.getDistance());
        }
        if (matchedSection.getDownStation().equals(addedSection.getDownStation())) {
            matchedSection.changeDownStation(addedSection.getUpStation());
            matchedSection.changeDistance(addedSection.getDistance());
        }
    }

    private void changeAroundStationByMatchStation(Section addedSection, Section matchedSection) {
        Optional<Section> aroundMatchedSection = sections.stream()
                .filter(section -> !matchedSection.equals(section))
                .filter(addedSection::anyMatch)
                .findFirst();

        if (aroundMatchedSection.isPresent()) {
            Section aroundSection = aroundMatchedSection.get();
            if (aroundSection.getDownStation().equals(matchedSection.getUpStation())) {
                aroundSection.changeDownStation(addedSection.getUpStation());
            } else {
                aroundSection.changeUpStation(addedSection.getDownStation());
            }
        }
    }


    public void deleteSection(Station station) {
        Section lastSection = sections.get(getLastIndex());

        if (!lastSection.matchDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역만 삭제 가능합니다.");
        }

        sections.remove(getLastIndex());
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findFirstUpStation();
        stations.add(downStation);

        while (sections.size() + 1 != stations.size()) {
            downStation = findNextLineStation(downStation).getDownStation();
            stations.add(downStation);
        }

        return Collections.unmodifiableList(stations);
    }

    private Station findFirstUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isStartStation)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section findNextLineStation(Station finalDownStation) {
        return sections.stream()
                .filter(it -> finalDownStation.equals(it.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isStartStation(Station station) {
        return sections.stream()
                .noneMatch(currentStation -> station.equals(currentStation.getDownStation()));
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public List<Section> getSections() {
        return sections.stream()
                .sorted((s1, s2) -> s1.getUpStation().getName().compareTo(s2.getDownStation().getName()))
                .collect(Collectors.toList());
    }
}
