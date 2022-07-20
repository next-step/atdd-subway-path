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
        sections.add(section);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, this.getUpStationTerminal());

        return stations;
    }

    private Station getUpStationTerminal() {
        return getUpSectionTerminal().getUpStation();
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
                .filter(section -> section.isSameSection(newSection)
                        || isNewSectionDownStationInLine(newSection, section))
                .findFirst()
                .ifPresent(section -> {
                    throw new IllegalArgumentException("구간이 중복되어 있으면 등록할 수 없습니다.");
                });
    }

    private boolean isNewSectionDownStationInLine(Section newSection, Section section) {
        return section.getUpStation() == newSection.getDownStation()
                || section.getDownStation() == newSection.getDownStation();
    }

    private Section findSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(e -> e.getDownStation().equals(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당되는 구간을 찾을 수 없습니다."));
    }
}
