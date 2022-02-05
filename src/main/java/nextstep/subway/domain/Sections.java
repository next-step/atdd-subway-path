package nextstep.subway.domain;

import nextstep.subway.exception.IllegalUpdatingStateException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public void addSection(Section section) {
        checkStateToAddSection(section);

        if (addSectionBetweenStations(section)) {
            return;
        }
        if (addSectionInFrontOfFirstUpStation(section)) {
            return;
        }
        addSectionBehindLastDownStation(section);
    }

    private void checkStateToAddSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }
        List<Station> allStations = getAllStations();
        if (allStations.contains(section.getUpStation())
                && allStations.contains(section.getDownStation())) {
            throw new IllegalUpdatingStateException("요청한 구간의 상행역과 하행역이 이미 노선에 등록되어있습니다.");
        }
    }

    private boolean addSectionBetweenStations(Section section) {
        if (addSectionBetweenStationsWhenSameUpStation(section)) {
            return true;
        }
        if (addSectionBetweenStationsWhenSameDownStation(section)) {
            return true;
        }
        return false;
    }

    private boolean addSectionBetweenStationsWhenSameUpStation(Section section) {
        Optional<Section> optionalSameUpStationSection = sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findAny();
        if (!optionalSameUpStationSection.isPresent()) {
            return false;
        }

        Section sameUpStationSection = optionalSameUpStationSection.get();
        sameUpStationSection.updateForSplittingBySameUpStationSection(section);
        sections.add(sections.indexOf(sameUpStationSection), section);
        return true;
    }

    private boolean addSectionBetweenStationsWhenSameDownStation(Section section) {
        Optional<Section> optionalSameDownStationSection = sections.stream()
                .filter(s -> s.getDownStation().equals(section.getDownStation()))
                .findAny();
        if (!optionalSameDownStationSection.isPresent()) {
            return false;
        }

        Section sameDownStationSection = optionalSameDownStationSection.get();
        sameDownStationSection.updateForSplittingBySameDownStationSection(section);
        sections.add(sections.indexOf(sameDownStationSection) + 1, section);
        return true;
    }

    private boolean addSectionInFrontOfFirstUpStation(Section section) {
        return false;
    }

    private boolean addSectionBehindLastDownStation(Section section) {
        sections.add(section);
        return true;
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> allStations = new ArrayList<>();
        allStations.add(sections.get(0).getUpStation());
        allStations.addAll(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
        return allStations;
    }

    private Station getLastDownStation() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public Section removeSection(Station station) {
        checkPossibleRemovingSection(station);
        return sections.remove(sections.size() - 1);
    }

    private void checkPossibleRemovingSection(Station station) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalUpdatingStateException("해당 노선의 구간이 " + MIN_SECTION_SIZE + "개 이하라 삭제하지 못합니다.");
        }
        if (!Objects.equals(getLastDownStation(), station)) {
            throw new IllegalUpdatingStateException("해당 노선의 하행 종점역이 아니라 삭제하지 못합니다.");
        }
    }

    public int getTotalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
