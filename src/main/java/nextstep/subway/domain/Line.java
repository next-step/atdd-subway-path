package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getSection(long upStationId, long downStationId) {

        return this.sections.stream()
                .filter(it -> it.getUpStation().getId() == upStationId && it.getDownStation().getId() == downStationId)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public void addSection(Station upStation, Station downStation, int distance) {

        Section newSection = new Section(this, upStation, downStation, distance);

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        validateAddedStation(upStation, downStation);

        if (isAdditionToEnd(upStation, downStation)) {
            this.sections.add(newSection);
            return;
        }

        if (addToUpToUpStation(upStation, newSection)) {
            return;
        }

        if (addDownToDownSection(downStation, newSection)) {
            return;
        }

        throw new RuntimeException("잘못된 데이터입니다.");
    }

    private boolean addToUpToUpStation(Station upStation, Section newSection) {

        Optional<Section> upStationMatchedSection = this.sections.stream().filter(it -> it.getUpStation() == upStation).findFirst();

        if (upStationMatchedSection.isEmpty()) {
            return false;
        }

        Section oldSection = upStationMatchedSection.get();
        splitSection(oldSection, newSection, newSection.getDownStation(), oldSection.getDownStation());

        return true;
    }

    private boolean addDownToDownSection(Station downStation, Section newSection) {

        Optional<Section> downStationMatchedSection = this.sections.stream().filter(it -> it.getDownStation() == downStation).findFirst();

        if (downStationMatchedSection.isEmpty()) {
            return false;
        }

        Section oldSection = downStationMatchedSection.get();
        splitSection(oldSection, newSection, oldSection.getUpStation(), newSection.getUpStation());

        return true;

    }

    private void validateAddedStation(Station upStation, Station downStation) {
        if (isIncludeBoth(upStation, downStation)) {
            throw new IllegalArgumentException("upStation과 downStation 모두 존재합니다.");
        }

        if (isNotIncludeBoth(upStation, downStation)) {
            throw new IllegalArgumentException("upStation과 downStation 모두 존재하지 않습니다.");
        }
    }

    /**
     * 
     * 구간을 두 개의 구간으로 나눈다.
     * 
     * @param oldSection 기존에 존재하던 구간 (삭제)
     * @param newSection 요청을 통해 추가되는 구간 (추가)
     * @param splitedUpStation 파생된 구간의 상행역
     * @param splitedDownStation 파생된 구간의 하행역
     */
    private void splitSection(Section oldSection, Section newSection, Station splitedUpStation, Station splitedDownStation) {

        if (oldSection.getDistance() - newSection.getDistance() <= 0) {
            throw new IllegalArgumentException("거리가 너무 짧거나 깁니다.");
        }

        this.sections.remove(oldSection);
        this.sections.add(newSection);
        this.sections.add(new Section(this, splitedUpStation, splitedDownStation, oldSection.getDistance() - newSection.getDistance()));
    }

    /**
     * 노선의 처음 또는 끝에 구간을 더하는 경우인지 체크한다.
     */
    private boolean isAdditionToEnd(Station upStation, Station downStation) {
        return this.sections.stream().anyMatch(it -> it.getDownStation() == upStation) || this.sections.stream()
                .anyMatch(it -> it.getUpStation() == downStation);
    }

    private boolean isIncludeBoth(Station upStation, Station downStation) {
        return getStations().contains(upStation) && getStations().contains(downStation);
    }

    private boolean isNotIncludeBoth(Station upStation, Station downStation) {
        return !getStations().contains(upStation) && !getStations().contains(downStation);
    }

    /**
     * station을 노선 상행에서 하행 순서대로 만든다.
     * @return 정렬된 정거장 목록
     */
    public List<Station> getStations() {

        if (this.sections.isEmpty()) {
            return List.of();
        }

        List<Station> result = new ArrayList<>();

        // map 생성. key: upStation, value: downStation
        Map<Station, Station> upDownStationMap = this.sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        // first Station을 찾는다.
        upDownStationMap.keySet().stream()
                .filter(station -> isFirstStation(upDownStationMap, station))
                .findFirst()
                .ifPresent(result::add);

        // 순서대로 list에 넣는다.
        Station nextStation = result.get(0);
        while (upDownStationMap.containsKey(nextStation)) {
            nextStation = upDownStationMap.get(nextStation);
            result.add(nextStation);
        }

        return Collections.unmodifiableList(result);
    }

    private static boolean isFirstStation(Map<Station, Station> upDownStationMap, Station upStation) {
        return upDownStationMap.values().stream().filter(downStation -> downStation == upStation).findFirst().isEmpty();
    }
}
