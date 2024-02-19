package nextstep.subway.domain;

import nextstep.subway.exception.line.LineSectionException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    protected Line() {
    }

    public static Line create(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name, color);
        return getSectionAddedLine(line, upStation, downStation, distance);
    }

    public static Line create(String name, String color) {
        return new Line(name, color);
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
        return getAllSections()
                .stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteStation(Long stationId) {
        validateDeleteStation(stationId);

        if (getFirstStation().getId().equals(stationId)) {
            sections.remove(getFirstSection());
            return;
        }

        if (getLastStation().getId().equals(stationId)) {
            sections.remove(getLastSection());
            return;
        }
        deleteMiddleSectionByStationId(stationId);
    }

    public void addSection(Section sectionToAdd) {
        if (sections.isEmpty()) {
            sections.add(sectionToAdd);
            return;
        }

        if (Objects.equals(sectionToAdd.getUpStation(), sectionToAdd.getDownStation())) {
            throw new LineSectionException("상행역과 하행역은 동일할 수 없습니다.");
        }

        if (isToBeFirstSection(sectionToAdd)) {
            addAsFirstSection(sectionToAdd);
            return;
        }

        Optional<Section> sectionWithSameUpStation = getSectionWithSameUpStation(sectionToAdd);
        Optional<Section> sectionWithSameDownStation = getSectionWithSameDownStation(sectionToAdd);

        if (sectionWithSameUpStation.isPresent()) {
            addAsMiddleSection(sectionToAdd, sectionWithSameUpStation.get());
            return;
        }
        if (sectionWithSameDownStation.isPresent()) {
            addAsMiddleSection(sectionToAdd, sectionWithSameDownStation.get());
            return;
        }

        addAsLastSection(sectionToAdd);
    }

    private void validateDeleteStation(Long stationId) {
        if (sections.size() <= 1) {
            throw new LineSectionException("삭제할 수 있는 구간이 없습니다.");
        }

        if (getAllStations().stream().noneMatch(station -> station.getId().equals(stationId))) {
            throw new LineSectionException("노선에 존재하지 않는 역입니다.");
        }
    }

    private Station getFirstStation() {
        return getAllStations().get(0);
    }

    private Section getFirstSection() {
        return getAllSections().get(0);
    }

    private Station getLastStation() {
        return getAllStations().get(getAllStations().size() - 1);
    }

    private Section getLastSection() {
        return getAllSections().get(getAllSections().size() - 1);
    }

    private boolean isToBeFirstSection(Section section) {
        return section.getDownStation().equals(getFirstStation());
    }

    private void addAsFirstSection(Section section) {
        if (getAllStations().contains(section.getUpStation())) {
            throw new LineSectionException("상행역은 기존 노선에 존재하는 역일 수 없습니다.");
        }
        sections.add(0, section);
    }

    private void addAsLastSection(Section section) {
        if (getAllStations().contains(section.getDownStation())) {
            throw new LineSectionException("노선의 마지막 구간을 추가하려는 경우 하행역은 기존 노선에 없는 역이여야 합니다.");
        }
        if (!section.getUpStation().equals(getLastStation())) {
            throw new LineSectionException("노선의 하행역만 상행역으로 지정 가능합니다.");
        }
        sections.add(section);
    }

    private void addAsMiddleSection(Section sectionToAdd, Section existingSection) {
        boolean isSameUpStation = existingSection.getUpStation().equals(sectionToAdd.getUpStation());
        boolean isSameDownStation = existingSection.getDownStation().equals(sectionToAdd.getDownStation());

        if (isSameUpStation && isSameDownStation) {
            throw new LineSectionException("이미 존재하는 구간입니다.");
        }
        if (existingSection.getDistance() <= sectionToAdd.getDistance()) {
            throw new LineSectionException("추가하려는 구간의 거리가 기존 구간보다 큽니다.");
        }

        if (isSameUpStation && !isSameDownStation) {
            Section splitExistingSection = Section.create(
                    sectionToAdd.getUpStation(),
                    existingSection.getDownStation(),
                    existingSection.getDistance() - sectionToAdd.getDistance());

            sections.remove(existingSection);
            sections.add(sectionToAdd);
            sections.add(splitExistingSection);
            return;
        }

        Section splitExistingSection = Section.create(
                existingSection.getUpStation(),
                sectionToAdd.getUpStation(),
                existingSection.getDistance() - sectionToAdd.getDistance());

        sections.remove(existingSection);
        sections.add(splitExistingSection);
        sections.add(sectionToAdd);
    }

    private void deleteMiddleSectionByStationId(Long stationId) {
        Section sectionWithStationIdAsDownStation = sections.stream()
                .filter(section -> section.getDownStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new LineSectionException("노선에 존재하지 않는 역입니다."));

        Section sectionWithStationIdAsUpStation = sections.stream()
                .filter(section -> section.getUpStation().equals(sectionWithStationIdAsDownStation.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new LineSectionException(stationId + " 역의 다음 구간이 존재하지 않습니다."));

        int deletedSectionsDistance = sectionWithStationIdAsUpStation.getDistance()
                + sectionWithStationIdAsDownStation.getDistance();

        Section sectionForReconnection = Section.create(
                sectionWithStationIdAsDownStation.getUpStation(),
                sectionWithStationIdAsUpStation.getDownStation(),
                deletedSectionsDistance);

        sections.remove(sectionWithStationIdAsDownStation);
        sections.remove(sectionWithStationIdAsUpStation);
        sections.add(sectionForReconnection);
    }

    private Optional<Section> getSectionWithSameUpStation(Section section) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findFirst();
    }

    private Optional<Section> getSectionWithSameDownStation(Section section) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(section.getDownStation()))
                .findFirst();
    }

    private List<Section> getAllSections() {
        return getSections().stream().sorted().collect(Collectors.toList());
    }
}
