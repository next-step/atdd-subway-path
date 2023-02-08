package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Line line, Section newSection) {
        if (isNew()) {
            this.sections.add(new Section(line, newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
            return;
        }
        this.sections.stream()
                .filter(addable(newSection))
                .peek(section -> {
                    // 상행, 하행 중복된 구간을 추가하는 경우
                    if (section.equals(newSection)) {
                        throw new IllegalArgumentException("상행, 하행이 중복된 구간을 등록할 수 없습니다.");
                    }
                })
                .findFirst()
                .ifPresentOrElse(section -> {
                    // 하행 종점 방향에 추가된 경우
                    if (isAddInFinalDownStation(newSection.getUpStation())) {
                        addInFinalSection(line, section, newSection);
                    }
                    // 상행 종점 방향에 추가된 경우
                    if (isAddInFinalUpStation(newSection.getDownStation())) {
                        addInFinalSection(line, newSection, section);
                    }
                    // 상행 방향에 추가한 경우
                    if (isAddInUpSection(section, newSection)) {
                        checkSectionDistance(newSection, section);
                        this.sections.add(new Section(line, newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
                        this.sections.add(new Section(line, newSection.getDownStation(), section.getDownStation(), section.getDistance() - newSection.getDistance()));
                    }
                    // 하행 방향에 추가한 경우
                    if (isAddInDownSection(section, newSection)) {
                        checkSectionDistance(newSection, section);
                        this.sections.add(new Section(line, section.getUpStation(), newSection.getUpStation(), section.getDistance() - newSection.getDistance()));
                        this.sections.add(new Section(line, newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
                    }
                    this.sections.remove(section);
                }, () -> {
                    // 상,하행 둘다 존재하지 않는 역인 경우
                    throw new IllegalArgumentException("노선에 존재하지 않는 구간은 추가할 수 없습니다.");
                });
    }

    private void checkSectionDistance(Section newSection, Section section) {
        if (section.isDistanceLessThanEquals(newSection)) {
            throw new IllegalArgumentException("기존 구간의 길이보다 긴 구간은 추가할 수 없습니다.");
        }
    }

    private boolean isNew() {
        return this.sections.isEmpty();
    }

    private Predicate<Section> addable(Section newSection) {
        return section -> isAddInFinalUpStation(newSection.getDownStation())
                || isAddInFinalDownStation(newSection.getUpStation())
                || isAddInUpSection(section, newSection)
                || isAddInDownSection(section, newSection);
    }

    private boolean isAddInFinalUpStation(Station downStation) {
        return getFinalUpStation().equals(downStation);
    }

    private boolean isAddInFinalDownStation(Station upStation) {
        return getFinalDownStation().equals(upStation);
    }

    private boolean isAddInUpSection(Section section, Section newSection) {
        return section.getUpStation().equals(newSection.getUpStation());
    }

    private boolean isAddInDownSection(Section section, Section newSection) {
        return section.getDownStation().equals(newSection.getDownStation());
    }

    private void addInFinalSection(Line line, Section upSection, Section downSection) {
        this.sections.add(new Section(line, upSection.getUpStation(), upSection.getDownStation(), upSection.getDistance()));
        this.sections.add(new Section(line, downSection.getUpStation(), downSection.getDownStation(), downSection.getDistance()));
    }

    public List<Section> get() {
        return sections;
    }

    public Stations getStations() {
        if (isNew()) {
            return Stations.of();
        }
        Stations stations = Stations.of(getFinalUpStation());
        Station finalDownStation = getFinalDownStation();
        while (!stations.isFinalDownStationEqualTo(finalDownStation)) {
            // 상행역 -> 하행역
            this.sections.stream()
                    .filter(section -> stations.isFinalDownStationEqualTo(section.getUpStation()))
                    .findFirst()
                    .map(Section::getDownStation)
                    .ifPresent(stations::add);
        }
        return stations;
    }

    private Station getFinalUpStation() {
        return getStationStream(Section::getUpStation)
                .filter(upStation -> !getDownStations().contains(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행 종점역이 존재하지 않습니다."));
    }

    private Station getFinalDownStation() {
        return getStationStream(Section::getDownStation)
                .filter(downStation -> !getUpStations().contains(downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("하행 종점역이 존재하지 않습니다."));
    }

    private List<Station> getDownStations() {
        return getStationStream(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getUpStations() {
        return getStationStream(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public List<Integer> getSectionDistances() {
        return getStationStream(Section::getDistance)
                .collect(Collectors.toList());
    }

    private <T> Stream<T> getStationStream(Function<Section, T> function) {
        return sections.stream().map(function);
    }

    public void removeSection(Station station) {
        if (!getFinalDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

    public void removeSection(String stationName) {
        sections.stream()
                .filter(section -> section.isStationNameEqualTo(stationName))
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }
}
