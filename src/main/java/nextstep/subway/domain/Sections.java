package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Line line, Section section) {
        boolean addInUpSection = exist(section.getUpStation());
        boolean addInDownSection = exist(section.getDownStation());
        addable(addInUpSection, addInDownSection);
        if (addInUpSection) {
            addInUp(section);
        }
        if (addInDownSection) {
            addInDown(section);
        }
        this.sections.add(new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    private void addable(boolean existInUpSection, boolean existInDownSection) {
        if (isExistBothSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("상행, 하행이 중복된 구간을 등록할 수 없습니다.");
        }
        if (isNotFoundSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("노선에 존재하지 않는 구간은 추가할 수 없습니다.");
        }
    }

    private boolean isNotFoundSection(boolean existInUpSection, boolean existInDownSection) {
        return !sections.isEmpty() && isExistBothSection(!existInUpSection, !existInDownSection);
    }

    private static boolean isExistBothSection(boolean existInUpSection, boolean existInDownSection) {
        return existInUpSection && existInDownSection;
    }

    private void addInUp(Section newSection) {
        findUpSection(newSection.getUpStation())
                .ifPresent(section -> section.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    private void addInDown(Section newSection) {
        findDownSection(newSection.getDownStation())
                .ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistance()));
    }

    public void remove(Station station) {
        Optional<Section> upSection = findUpSection(station);
        Optional<Section> downSection = findDownSection(station);

        removable(upSection.isPresent(), downSection.isPresent());
        if (upSection.isPresent() && downSection.isPresent()) {
            Section section = upSection.get();
            mergeSection(section);
            return;
        }
        upSection.ifPresent(this::removeSection);
        downSection.ifPresent(this::removeSection);
    }

    private void removable(boolean existInUpSection, boolean existInDownSection) {
        if (isNotFoundSection(existInUpSection, existInDownSection)) {
            throw new IllegalArgumentException("존재하지 않는 구간은 삭제할 수 없습니다.");
        }
    }

    private void mergeSection(Section targetSection) {
        findDownSection(targetSection.getUpStation())
                .ifPresent(section -> section.updateDownStation(targetSection.getDownStation(), -targetSection.getDistance()));
        removeSection(targetSection);
    }

    private void removeSection(Section section) {
        if (isLeftOneSection()) {
            throw new IllegalArgumentException("노선에 구간이 하나 남아 삭제할 수 없습니다.");
        }
        this.sections.remove(section);
    }

    private boolean isLeftOneSection() {
        return sections.size() == 1;
    }

    private boolean exist(Station station) {
        return sections.stream()
                .anyMatch(section -> section.has(station));
    }

    public List<Section> getSections() {
        return sections;
    }

    public Optional<Section> findUpSection(Station station) {
        return findSection((section) -> section.isUpStationEquals(station));
    }

    public Optional<Section> findDownSection(Station station) {
        return findSection((section) -> section.isDownStationEquals(station));
    }

    private Optional<Section> findSection(Predicate<Section> equals) {
        return sections.stream()
                .filter(equals)
                .findFirst();
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        Station station = findFinalUpStation();
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
        return findUpSection(station)
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    private Station findFinalUpStation() {
        Station finalUpStation = getFinalUpStation();
        while (existPreSection(finalUpStation)) {
            Section preSection = findPreSection(finalUpStation);
            finalUpStation = preSection.getUpStation();
        }
        return finalUpStation;
    }

    private Station getFinalUpStation() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException("구간이 존재하지 않습니다.");
        }
        return this.sections.get(0).getUpStation();
    }

    private boolean existPreSection(Station station) {
        return this.sections.stream()
                .filter(Section::isExistDownStation)
                .anyMatch(section -> section.isDownStationEquals(station));
    }

    private Section findPreSection(Station station) {
        return findDownSection(station)
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 존재하지 않습니다."));
    }

    public List<Integer> getSectionDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }
}
