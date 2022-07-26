package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sections {

    @OrderColumn
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();
    private static int MIN_SIZE = 1;

    public void add(Section section) {
        if (values.isEmpty()) {
            values.add(section);
            return;
        }

        ensureCanAddition(section);

        if(canChangeMiddleSection(section)) {
            Section originSection = findSameStationSection(section);
            originSection.changeSectionConditionBy(section);
        }

        values.add(section);
    }

    public void remove(Station station) {
        ensureCanRemove(station);
        values.remove(getDownTerminus());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<Station> findConnectedStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station upStation = findUpTerminus();
        stations.add(upStation);

        while(!isDownTerminus(upStation)) {
            upStation = findNextStation(upStation);
            stations.add(upStation);
        }

        return stations;
    }


    public Station findUpTerminus() {
        return values.stream()
              .map(Section::getUpStation)
              .filter(this::isUpTerminus)
              .findAny()
              .orElseThrow(SubwayException::new);
    }

    public Station findNextStation(Station upStation) {
        return values.stream()
                     .filter(s -> s.getUpStation().equals(upStation))
                     .map(Section::getDownStation)
                     .findAny()
                     .orElseThrow(IllegalArgumentException::new);
    }

    private List<Station> findAllStations() {
        List<Station> stations = values.stream()
                                      .map(Section::getUpStation)
                                      .collect(Collectors.toList());

        Section downTerminus = getDownTerminus();
        stations.add(downTerminus.getDownStation());
        return stations;
    }

    public Section findSameStationSection(Section section) {
        return  values.stream()
                      .filter(hasSameStationToOriginSection(section))
                      .findAny()
                      .orElseThrow(IllegalArgumentException::new);
    }

    private boolean canChangeMiddleSection(Section newSection) {
        return values.stream().anyMatch(hasSameStationToOriginSection(newSection));
    }

    private Predicate<Section> hasSameStationToOriginSection(Section newSection) {
        return s -> s.isEqualToUpStation(newSection.getUpStation())
                || s.isEqualToDownStation(newSection.getDownStation());
    }

    private Section getDownTerminus() {
        return values.stream()
                     .filter(s -> isDownTerminus(s.getDownStation()))
                     .findFirst()
                     .orElseThrow(IllegalAccessError::new);
    }

    private boolean isUpTerminus(Station upStation) {
        return values.stream().noneMatch( s -> s.getDownStation().equals(upStation));
    }

    private boolean isDownTerminus(Station station) {
        return values.stream().noneMatch( s -> s.getUpStation().equals(station));
    }

    private boolean existAllStation(Section section) {
        return new HashSet<>(findConnectedStations()).containsAll(section.getStations());
   }


    private void ensureCanRemove(Station station) {
        if (values.size() <= MIN_SIZE) {
            throw new IllegalArgumentException("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
        }

        if (!getDownTerminus().isEqualToDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 제거할 수 없습니다.");
        }
    }

    private boolean hasStation(Section section) {
        return findAllStations().stream().anyMatch(
                s1 -> section.getStations().stream().anyMatch(s1::equals)
        );
    }

    private void ensureCanAddition(Section section) {
        if (existAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록 되어있는 구간은 등록할 수 없습니다.");
        }
        if (!hasStation(section)) {
            throw new IllegalArgumentException("상행역이나 하행역 중 하나는 등록된 구간에 포함되어야 합니다.");
        }
    }
}
