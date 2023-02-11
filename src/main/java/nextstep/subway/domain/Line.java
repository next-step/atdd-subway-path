package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void validateSection(Section section) {
        if(sections.isEmpty()) {
            return;
        }

        validateMatchingSection(section);
        validateUnmatchingSection(section);
        validateDistance(section);
    }



    private boolean hasUpToUp(Station upStation) {
        return findUpToUp(upStation).isPresent();
    }

    private boolean hasDownToDown(Station downStation) {
        return findDownToDown(downStation).isPresent();
    }

    private void addUpToUp(Section section) {
        findUpToUp(section.getUpStation())
                .ifPresent(it -> {
                    sections.remove(it);
                    sections.add(section);
                    sections.add(new Section(this, section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                });
    }

    private void addDownToDown(Section section) {
        findDownToDown(section.getDownStation()).ifPresent(it -> {
            sections.remove(it);
            sections.add(new Section(this, it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()));
            sections.add(section);
        });
    }

    public void addSection(Section section) {
        validateSection(section);

        if(hasUpToUp(section.getUpStation())) {
            addUpToUp(section);
            return;
        }

        if(hasDownToDown(section.getDownStation())) {
            addDownToDown(section);
            return;
        }

        if(hasDownToBeginUp(section.getDownStation())) {
            addDownToBeginUp(section);
            return;
        }

        if(hasUpToEndDown(section.getUpStation())) {
            addUpToEndDown(section);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if(sections.isEmpty()) {
            return stations;
        }

        sections.sort(Comparator.comparingInt(Section::getOrder));
        stations.add(findFirstSection().getUpStation());

        sections.forEach((it) -> stations.add(it.getDownStation()));

        return stations;
    }

    public int getGreatestOrder() {
        return sections.stream().max(Comparator.comparing(Section::getOrder))
                .orElse(new Section()).getOrder();
    }

    private void validateMatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(matchUpStation && matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void validateUnmatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(!matchUpStation && !matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void validateDistance(Section section) {
        validateUpStationDistance(section);
        validateDownStationDistance(section);
    }

    private void validateUpStationDistance(Section section) {
        findUpToUp(section.getUpStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private void validateDownStationDistance(Section section) {
        findDownToDown(section.getDownStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private boolean hasMatchingStation(Station station) {
        return sections.stream()
                .anyMatch((it) -> station.equals(it.getUpStation()) || station.equals(it.getDownStation()));
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

    private Section findFirstSection() {
        return sections.stream().min(Comparator.comparing(Section::getOrder))
                .orElseThrow(() -> new EntityNotFoundException("노선에 구간이 없습니다"));
    }

    private Section findLastSection() {
        return sections.stream().max(Comparator.comparing(Section::getOrder))
                .orElseThrow(() -> new EntityNotFoundException("노선에 구간이 없습니다"));
    }

    private boolean hasDownToBeginUp(Station downStation) {
        Section beginSection = findFirstSection();
        return downStation.equals(beginSection.getUpStation());
    }

    private boolean hasUpToEndDown(Station upStation) {
        Section endSection = findLastSection();
        return upStation.equals(endSection.getDownStation());
    }

    private void addDownToBeginUp(Section section) {
        sections.forEach(Section::increaseOrder);
        section.beginOrder();
        sections.add(section);
    }

    private void addUpToEndDown(Section section) {
        sections.add(section);
    }
}
