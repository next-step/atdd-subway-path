package nextstep.subway.domain.line;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        Section section = sections.get(0);
        List<Station> stations = new ArrayList<>();
        stations.addAll(getUpperStations(section.getUpStation()));
        stations.addAll(getDownerStations(section.getDownStation()));

        return Collections.unmodifiableList(stations);
    }

    private List<Station> getUpperStations(Station upStation) {
        List<Station> list = new ArrayList<>();
        list.add(upStation);
        Optional<Section> maybeUpSection = sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .findFirst();
        while (maybeUpSection.isPresent()) {
            Section upSection = maybeUpSection.get();
            Station upperStation = upSection.getUpStation();
            list.add(upperStation);
            maybeUpSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(upperStation))
                    .findFirst();
        }
        Collections.reverse(list);
        return list;
    }

    private List<Station> getDownerStations(Station downStation) {
        List<Station> list = new ArrayList<>();
        list.add(downStation);
        Optional<Section> maybeDownSection = sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst();
        while (maybeDownSection.isPresent()) {
            Section downSection = maybeDownSection.get();
            Station downerStation = downSection.getDownStation();
            list.add(downerStation);
            maybeDownSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(downerStation))
                    .findFirst();
        }
        return list;
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        boolean isUpStationExist = sections.stream()
                .anyMatch(s -> s.getUpStation().equals(newSection.getUpStation())
                        || s.getDownStation().equals(newSection.getUpStation()));
        boolean isDownStationExist = sections.stream()
                .anyMatch(s -> s.getUpStation().equals(newSection.getDownStation())
                        || s.getDownStation().equals(newSection.getDownStation()));
        if (isUpStationExist && isDownStationExist) {
            throw new IllegalArgumentException("노선에 존재하던 지하철 역 끼리는 구간을 만들 수 없습니다.");
        }
        if (!isUpStationExist && !isDownStationExist) {
            throw new IllegalArgumentException("새로운 구간의 지하철 역들 중 하나는 기존 지하철 노선에 포함되어 있어야 합니다.");
        }

        if (isUpStationExist) {
            addUpAndDownSection(newSection);
            return;
        }
        addDownAndUpSection(newSection);
    }

    private void addUpAndDownSection(Section newSection) {
        Optional<Section> maybeExistSection = sections.stream()
                .filter(s -> s.getUpStation().equals(newSection.getUpStation()))
                .findFirst();
        if (maybeExistSection.isPresent()) {
            addUpAndDownSectionBetween(newSection, maybeExistSection.get());
            return;
        }
        sections.add(newSection);
    }

    private void addUpAndDownSectionBetween(Section newSection, Section section) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.");
        }
        sections.remove(section);
        sections.add(newSection);
        sections.add(new Section(section.getLine(), newSection.getDownStation(), section.getDownStation(), section.getDistance() - newSection.getDistance()));
    }

    private void addDownAndUpSection(Section newSection) {
        Optional<Section> maybeExistSection = sections.stream()
                .filter(s -> s.getDownStation().equals(newSection.getDownStation()))
                .findFirst();
        if (maybeExistSection.isPresent()) {
            addDownAndUpSectionBetween(newSection, maybeExistSection.get());
            return;
        }
        sections.add(newSection);
    }

    private void addDownAndUpSectionBetween(Section newSection, Section section) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.");
        }
        sections.remove(section);
        sections.add(new Section(section.getLine(), section.getUpStation(), newSection.getUpStation(), section.getDistance() - newSection.getDistance()));
        sections.add(newSection);
    }

    public void removeSection(Station station) {
        List<Station> stations = getStations();
        Station lastStation = stations.get(stations.size() - 1);
        if (!lastStation.equals(station)) {
            throw new IllegalArgumentException();
        }
        Section lastSection = sections.stream().filter(s -> s.getDownStation().equals(lastStation))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
        sections.remove(lastSection);
    }
}
