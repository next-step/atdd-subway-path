package nextstep.subway.domain;

import nextstep.subway.handler.exception.SectionException;
import nextstep.subway.handler.validator.SectionValidator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static nextstep.subway.handler.exception.ErrorCode.NO_CORRECT_SECTION;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    public Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        sections.add(initSection(upStation, downStation, distance));
    }

    private Section initSection(Station upStation, Station downStation, int distance) {
        return Section.of(this, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    /* getter */
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

    /* 구간 추가 */
    public void addSection(Section section) {
        if (isUpStation(section.getDownStation())) {
            this.upStation = section.getUpStation();
        }

        if (isDownStation(section.getUpStation())) {
            this.downStation = section.getDownStation();
        }

        insertSection(section);
    }

    private void insertSection(Section insertedSection) {
        bothUpStationSame(insertedSection);
        bothDownStationSame(insertedSection);

        sections.add(insertedSection);
    }

    private void bothUpStationSame(Section insertedSection) {
        sections.stream()
                .filter(oldSection -> sameUpDifferentDown(insertedSection, oldSection))
                .findFirst()
                .ifPresent(oldSection -> pushNewRemoveOld(insertedSection.getDownStation(), oldSection.getDownStation(),
                        extractDistance(insertedSection, oldSection), oldSection));
    }

    private boolean sameUpDifferentDown(Section insertedSection, Section oldSection) {
        return oldSection.isUpStation(insertedSection.getUpStation())
                && oldSection.isNotDownStation(insertedSection.getDownStation());
    }

    private void bothDownStationSame(Section insertedSection) {
        sections.stream()
                .filter(oldSection -> sameDownDifferentUp(insertedSection, oldSection))
                .findFirst()
                .ifPresent(oldSection -> pushNewRemoveOld(oldSection.getUpStation(), insertedSection.getUpStation(),
                        extractDistance(insertedSection, oldSection), oldSection));
    }

    private boolean sameDownDifferentUp(Section insertedSection, Section oldSection) {
        return oldSection.isDownStation(insertedSection.getDownStation())
                && oldSection.isNotUpStation(insertedSection.getUpStation());
    }

    private int extractDistance(Section insertedSection, Section oldSection) {
        return oldSection.getDistance() - insertedSection.getDistance();
    }

    private void pushNewRemoveOld(Station upStation, Station downStation, int distance, Section oldSection) {
        pushSection(upStation, downStation, distance);
        sections.remove(oldSection);
    }

    private void pushSection(Station upStation, Station downStation, int newDistance) {
        SectionValidator.validateDistance(newDistance);
        sections.add(Section.of(this, upStation, downStation, newDistance));
    }

    /* 갖고있는 지하철역 리스트 반환 */
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        List<Section> tmpSections = new ArrayList<>(sections);
        Station tmpStation = upStation;

        while (!tmpSections.isEmpty()) {
            for (Section section : tmpSections) {
                if (tmpStation.equals(section.getUpStation())) {
                    stations.add(section.getUpStation());
                    tmpStation = section.getDownStation();
                    tmpSections.remove(section);
                    break;
                }
            }
        }

        stations.add(tmpStation);
        return stations.stream().distinct().collect(Collectors.toList());
    }

    /* 노선 정보 변경 */
    public void update(String name, String color) {
        if (name != null && !this.name.equals(name)) {
            this.name = name;
        }
        if (color != null && !this.color.equals(color)) {
            this.color = color;
        }
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean hasStation(Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void removeSection(Section targetSection) {
        sections.remove(targetSection);
    }

    public Section findSectionByStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst()
                .orElseThrow(() -> new SectionException(NO_CORRECT_SECTION));
    }
}
