package nextstep.subway.domain;

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
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addNew(Line line, Section section) {
        this.sections.add(new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    public void addInUp(Section newSection) {
        this.sections.stream()
                .filter(section -> section.isUpStationEquals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    public void addInDown(Section newSection) {
        this.sections.stream()
                .filter(section -> section.isDownStationEquals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistance()));
    }

    public void mergeSection(Section targetSection) {
        this.sections.stream()
                .filter(section -> section.isDownStationEquals(targetSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(targetSection.getDownStation(), -targetSection.getDistance()));
        removeSection(targetSection);
    }

    public void removeSection(Section section) {
        if (isLeftOneSection()) {
            throw new IllegalArgumentException("노선에 구간이 하나 남아 삭제할 수 없습니다.");
        }
        this.sections.remove(section);
    }

    private boolean isLeftOneSection() {
        return sections.size() == 1;
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean exist(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStationEquals(station)
                        || section.isDownStationEquals(station));
    }

    public List<Section> getSections() {
        return sections;
    }

    public Optional<Section> findUpSection(Station station) {
        return sections.stream()
                .filter(section -> section.isUpStationEquals(station))
                .findFirst();
    }

    public Optional<Section> findDownSection(Station station) {
        return sections.stream()
                .filter(section -> section.isDownStationEquals(station))
                .findFirst();
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        Station station = getFinalUpStation();
        stations.add(station);
        while (existNextSection(station)) {
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    private boolean existNextSection(Station station) {
        return this.sections.stream()
                .filter(Section::isExistUpStation)
                .anyMatch(section -> section.isUpStationEquals(station));
    }

    private Section findNextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isUpStationEquals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    private Station getFinalUpStation() {
        Station finalUpStation = this.sections.get(0).getUpStation();
        while (existPreSection(finalUpStation)) {
            Section preSection = findPreSection(finalUpStation);
            finalUpStation = preSection.getUpStation();
        }
        return finalUpStation;
    }

    private boolean existPreSection(Station station) {
        return this.sections.stream()
                .filter(Section::isExistDownStation)
                .anyMatch(section -> section.isDownStationEquals(station));
    }

    private Section findPreSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isDownStationEquals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 존재하지 않습니다."));
    }

    public List<Integer> getSectionDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }
}
