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

        addDownToDownSection(downStation, newSection);
    }

    private boolean addToUpToUpStation(Station upStation, Section newSection) {
        Optional<Section> upStationMatchedSection = this.sections.stream().filter(it -> it.getUpStation() == upStation).findFirst();
        if (upStationMatchedSection.isPresent()) {
            Section oldSection = upStationMatchedSection.get();
            splitSection(oldSection, newSection, newSection.getDownStation(), oldSection.getDownStation());
            return true;
        }
        return false;
    }

    private void addDownToDownSection(Station downStation, Section newSection) {
        Optional<Section> downStationMatchedSection = this.sections.stream().filter(it -> it.getDownStation() == downStation).findFirst();
        if (downStationMatchedSection.isPresent()) {
            Section oldSection = downStationMatchedSection.get();
            splitSection(oldSection, newSection, oldSection.getUpStation(), newSection.getUpStation());
        }
    }

    private void validateAddedStation(Station upStation, Station downStation) {
        if (isIncludeAllStation(upStation, downStation)) {
            throw new IllegalArgumentException("upStation과 downStation 모두 존재합니다.");
        }

        if (isNotIncludeAnyStation(upStation, downStation)) {
            throw new IllegalArgumentException("upStation과 downStation 모두 존재하지 않습니다.");
        }
    }

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

    private boolean isIncludeAllStation(Station upStation, Station downStation) {
        return getStations().contains(upStation) && getStations().contains(downStation);
    }

    private boolean isNotIncludeAnyStation(Station upStation, Station downStation) {
        return !getStations().contains(upStation) && !getStations().contains(downStation);
    }

    public List<Station> getStations() {

        if (this.sections.isEmpty()) {
            return List.of();
        }

        List<Station> result = new ArrayList<>();

        Map<Station, Station> upDownStationMap = this.sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        // first Station을 찾는다.
        upDownStationMap.keySet().stream()
                .filter(upStation -> upDownStationMap.values().stream().filter(downStation -> downStation == upStation)
                        .findFirst().isEmpty()).findFirst().ifPresent(result::add);

        // 순서대로 list에 넣는다.
        Station nextStation = result.get(0);
        while (upDownStationMap.containsKey(nextStation)) {
            nextStation = upDownStationMap.get(nextStation);
            result.add(nextStation);
        }

        return Collections.unmodifiableList(result);
    }
}
