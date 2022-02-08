package nextstep.subway.domain;

import nextstep.subway.ui.exception.SectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    void addSection(Section newSection) {
        validateAddSectionStationNotExistInSection(newSection);
        for (Section existingSection : sections) {
            existingSection.updateAddLineBetweenSection(newSection);
        }
        sections.add(newSection);
    }

    void remove(Station downStation) {
        validateOneSection();
        Section removeSection = sections.stream()
                .filter(equalsDownStation(downStation))
                .findFirst()
                .orElseThrow(() ->
                        new SectionException(String.format("상행역과 하행역 모두 구간에 존재하지 않는 역입니다. 하행역 = %s", downStation)));
        sections.remove(removeSection);

        for (Section section : sections) {
            section.updateRemoveLineBetweenSection(removeSection);
        }
    }

    List<Station> getStations() {
        return getStations(getFirstSection());
    }

    boolean isEmpty() {
        return sections.isEmpty();
    }

    Station getDownStation(int sectionIndex) {
        return sections.get(sectionIndex).getDownStation();
    }

    int size() {
        return sections.size();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    private Predicate<Section> equalsDownStation(Station downStation) {
        return section -> section.getDownStation().equals(downStation);
    }

    private void validateAddSectionStationNotExistInSection(Section section) {
        if (!getUpStations().contains(section.getUpStation()) &&
                !getUpStations().contains(section.getDownStation()) &&
                !getDownStations().contains(section.getUpStation()) &&
                !getDownStations().contains(section.getDownStation()) &&
                !sections.isEmpty()) {
            throw new SectionException(
                    String.format("상행역과 하행역 모두 구간에 존재하지 않는 역입니다. 상행역 = %s, 하행역 = %s",
                            section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

    private void validateOneSection() {
        if (sections.size() <= 1) {
            throw new SectionException("구간이 1개 이하인 경우 삭제할 수 없습니다.");
        }
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

    private List<Station> getStations(Section firstSection) {
        List<Station> stations = new ArrayList<>();
        addEndUpSectionStation(firstSection, stations);
        addEndDownStation(stations);
        return stations;
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(section ->
                        getDownStations().stream()
                        .noneMatch(equalsUpAndDownStation(section)))
                .findFirst()
                .orElse(sections.get(0));
    }

    private Predicate<Station> equalsUpAndDownStation(Section section) {
        return downStation -> downStation.equals(section.getUpStation());
    }

    /**
     *
     * @param firstSection : getFirstSection()에서 가져온 상행 종점 구간이다.
     * @param stations : 빈 객체로 최초로 상행 종점 구간의 역을 추가할 객체이다.
     */
    private void addEndUpSectionStation(Section firstSection, List<Station> stations) {
        stations.add(firstSection.getUpStation());
        stations.add(firstSection.getDownStation());
    }

    /**
     * 상행 종점 구간의 하행 종점역 부터 시작하여 추가되는 하행 종점역과 각 구간의 상행역이 같으면 각 구간의 하행역을 추가한다.
     * @param stations : getFirstSection()에서 가져온 상행 종점 구간의 상행 종점역과 하행 종점역
     */
    private void addEndDownStation(List<Station> stations) {
        for (int i = 0; i < stations.size(); i++) {
            for (Section section : sections) {
                Station endDownStation = stations.get(stations.size() - 1);
                if (endDownStation.equals(section.getUpStation())) {
                    stations.add(section.getDownStation());
                }
            }
        }
    }
}
