package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;

        Section section = new Section(this, upStation, downStation, distance);
        this.sections.add(section);
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

    public void addSection(Station upStation, Station downStation, int distance) {
        Section newSection = new Section(this, upStation, downStation, distance);

        validateMatchingSection(newSection);
        validateUnmatchingSection(newSection);

        Optional<Section> sameUpStationSection = findUpToUp(upStation);
        if(sameUpStationSection.isPresent()) {
            addUpToUp(sameUpStationSection.get(), newSection);
            return;
        }

        Optional<Section> sameDownStationSection = findDownToDown(downStation);
        if(sameDownStationSection.isPresent()) {
            addDownToDown(sameDownStationSection.get(), newSection);
            return;
        }

        if(hasDownToBeginUp(downStation)) {
            addDownToBeginUp(newSection);
            return;
        }

        if(hasUpToEndDown(upStation)) {
            addUpToEndDown(newSection);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if(sections.isEmpty()) {
            return stations;
        }

        stations.add(getFirstSection().getUpStation());

        sections.forEach((it) -> stations.add(it.getDownStation()));

        return stations;
    }

    private void validateMatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(matchUpStation && matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void validateDistance(Section oldSection, Section newSection) {
        if(oldSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    private void validateUnmatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(!matchUpStation && !matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private Optional<Section> findUpToUp(Station upStation) {
        return sections.stream()
                .filter(it -> upStation.equals(it.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findDownToDown(Station downStation) {
        return sections.stream()
                .filter(it -> downStation.equals(it.getDownStation()))
                .findFirst();
    }

    private boolean hasDownToBeginUp(Station downStation) {
        Section beginSection = getFirstSection();

        return downStation.equals(beginSection.getUpStation());
    }

    private boolean hasUpToEndDown(Station upStation) {
        Section endSection = getLastSection();

        return upStation.equals(endSection.getDownStation());
    }

    private void addUpToUp(Section oldSection, Section newSection) {
        validateDistance(oldSection, newSection);

        sections.add(newSection);
        sections.add(
                new Section(this,
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance() - newSection.getDistance())
        );
        sections.remove(oldSection);
    }

    private void addDownToDown(Section oldSection, Section newSection) {
        validateDistance(oldSection, newSection);

        sections.remove(oldSection);
        sections.add(
                new Section(this,
                        oldSection.getUpStation(),
                        newSection.getUpStation(),
                        oldSection.getDistance() - newSection.getDistance())
        );
        sections.add(newSection);
    }

    private void addDownToBeginUp(Section newSection) {
        sections.add(0, newSection);
    }

    private void addUpToEndDown(Section newSection) {
        sections.add(newSection);
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private boolean hasMatchingStation(Station station) {
        return sections.stream()
                .anyMatch((it) -> station.equals(it.getUpStation()) || station.equals(it.getDownStation()));
    }
}
