package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }
        checkDuplicationSection(section);
        checkAlreadyRestedStation(section);

        sectionContainDownStation(section);
        sectionContainUpStation(section);

        sections.add(section);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Station upStationTerminal = getUpStationTerminal();
        Station downStationTerminal = getDownStationTerminal();

        List<Station> stations = new ArrayList<>();

        Station upStation = upStationTerminal;
        while (true) {
            Section section = findSectionByUpStation(upStation);
            stations.add(section.getUpStation());
            if (downStationTerminal.equals(section.getDownStation())) {
                stations.add(section.getDownStation());
                break;
            }
            upStation = section.getDownStation();
        }

        return stations;
    }

    private void sectionContainDownStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isSameDownStation(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> {
                    if (newSection.isLongerThan(section)) {
                        throw new IllegalArgumentException("새로운 추가되는 구간은 기존 구간보다 길 수 없습니다.");
                    }
                    sections.add(new Section(section.getLine(), section.getUpStation(), newSection.getUpStation(),
                            section.getDistance() - newSection.getDistance()));
                    sections.remove(section);
                });
    }

    private void sectionContainUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isSameUpStation(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> {
                    if (newSection.isLongerThan(section)) {
                        throw new IllegalArgumentException("새로운 추가되는 구간은 기존 구간보다 길 수 없습니다.");
                    }
                    sections.add(new Section(section.getLine(), newSection.getDownStation(), section.getDownStation(),
                            section.getDistance() - newSection.getDistance()));
                    sections.remove(section);
                });
    }

    private Station getUpStationTerminal() {
        return getUpSectionTerminal().getUpStation();
    }

    private Station getDownStationTerminal() {
        return getDownSectionTerminal().getDownStation();
    }

    private Section getUpSectionTerminal() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        upStations.removeAll(downStations);
        Station upStation = upStations.get(0);
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .collect(Collectors.toList())
                .get(0);
    }

    private Section getDownSectionTerminal() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        downStations.removeAll(upStations);
        Station downStation = downStations.get(0);
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .collect(Collectors.toList())
                .get(0);
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

    public void delete(Station station) {
        if (this.sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("지하철 구간은 최소 1개 이상 있어야합니다.");
        }

        Section section = findSectionByDownStation(station);
        sections.remove(section);
    }

    private void checkDuplicationSection(Section newSection) {
        this.sections.stream()
                .filter(section -> section.isSameSection(newSection))
                .findFirst()
                .ifPresent(section -> {
                    throw new IllegalArgumentException("구간이 중복되어 있으면 등록할 수 없습니다.");
                });
    }

    private void checkAlreadyRestedStation(Section newSection) {
        if (this.getStations().contains(newSection.getUpStation())
                && this.getStations().contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역이 둘 다 포함되어 있습니다.");
        }
    }

    private Section findSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(e -> e.getDownStation().equals(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당되는 구간을 찾을 수 없습니다."));
    }

    private Section findSectionByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(e -> e.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당되는 구간을 찾을 수 없습니다."));
    }
}
