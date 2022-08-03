package nextstep.subway.domain;

import nextstep.subway.exception.AllStationsOfSectionExistException;
import nextstep.subway.exception.InvalidDistanceOfSectionException;
import nextstep.subway.exception.NonStationOfSectionExistsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkDuplicateSection(section);
        checkIfNoStationOfSectionExists(section);
        checkIfDistanceOfSectionIsInvalid(section);

        rearrangeSectionWithUpStation(section);
        sections.add(section);
    }

    private void checkDuplicateSection(Section section) {
        sections.stream()
                .filter(it -> it.hasDuplicateSection(section.getUpStation(), section.getDownStation()))
                .findFirst()
                .ifPresent(it -> {
                    throw new AllStationsOfSectionExistException("신규 구간의 역이 이미 존재합니다.");
                });
    }

    private void checkIfNoStationOfSectionExists(Section section) {
        getStations().stream()
                .filter(station -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new NonStationOfSectionExistsException("신규 구간의 역과 일치하는 역이 존재하지 않습니다."));
    }

    private void checkIfDistanceOfSectionIsInvalid(Section section) {
        sections.stream()
                .filter(it -> {
                    return (it.getUpStation().equals(section.getUpStation()) && it.getDistance() <= section.getDistance())
                            || (it.getDownStation().equals(section.getDownStation()) && it.getDistance() <= section.getDistance());
                })
                .findFirst()
                .ifPresent(it -> {
                    throw new InvalidDistanceOfSectionException("구간 거리가 같거나 커 역 중간에 등록이 불가합니다.");
                });
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void rearrangeSectionWithUpStation(Section section) {
        sections.stream()
                .filter(it -> it.isSameUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> {
                    // 신규 구간의 하행역을 추가하고 기존 구간을 삭제한다.
                    sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                    sections.remove(it);
                });
    }

    public Station getLastStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void removeSection(Long stationId) {
        sections.removeIf(section -> section.getDownStation().compare(stationId));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
