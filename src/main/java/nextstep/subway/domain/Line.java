package nextstep.subway.domain;

import nextstep.subway.exception.LineSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    // TODO: Line 도메인 안에 Section 관리 로직을 분리할 수 있도록 Embedded 적용하기
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    protected Line() {}

    public static Line create(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name, color);
        return getSectionAddedLine(line, upStation, downStation, distance);
    }

    public static Line createWithId(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(id, name, color);
        return getSectionAddedLine(line, upStation, downStation, distance);
    }

    private static Line getSectionAddedLine(Line line, Station upStation, Station downStation, int distance) {
        Section section = Section.create(upStation, downStation, distance);
        line.sections.add(section);
        return line;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getAllStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public Station getTheMostDownStation() {
        Section theMostDownSection = sections.get(sections.size() - 1);
        return theMostDownSection.getDownStation();
    }

    public void deleteStation(Long stationId) {
        if (sections.size() <= 1) {
            throw new LineSectionException("삭제할 수 있는 구간이 없습니다.");
        }
        if (!Objects.equals(getTheMostDownStation().getId(), stationId)) {
            throw new LineSectionException("노선의 하행역만 삭제할 수 있습니다.");
        }
        sections.remove(sections.size() - 1);
    }

    public void addSection(Section section) {
        validateStationsOfSection(section);
        sections.add(section);
    }

    private void validateStationsOfSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }
        if (Objects.equals(section.getUpStation(), section.getDownStation())) {
            throw new LineSectionException("상행역과 하행역을 동일할 수 없습니다.");
        }
        if (isDownStationAlreadyIncluded(section.getDownStation())) {
            throw new LineSectionException("이미 노선에 포함된 역은 하행역으로 지정할 수 없습니다.");
        }
        if (isUpStationDifferentFromCurrentDownStation(section.getUpStation())) {
            throw new LineSectionException("노선의 하행역만 상행역으로 지정 가능합니다.");
        }
    }

    private Boolean isDownStationAlreadyIncluded(Station downStation) {
        return getAllStations().contains(downStation);
    }

    private Boolean isUpStationDifferentFromCurrentDownStation(Station upStation) {
        Section lastSection = sections.get(sections.size() - 1);
        return !upStation.equals(lastSection.getDownStation());
    }
}
