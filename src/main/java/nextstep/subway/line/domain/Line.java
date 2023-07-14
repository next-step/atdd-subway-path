package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.util.StringUtils;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionList;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    private Integer distance;

    @Embedded
    private LineLastStations lastStations;

    @Embedded
    private SectionList sections;

    protected Line() {}

    public Line(Long id, String name, String color, Integer distance, LineLastStations lastStations,
        SectionList sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.lastStations = lastStations;
        this.sections = sections;
    }

    public Line(String name, String color, LineLastStations lastStations, Integer distance) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(color)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.name = name;
        this.color = color;
        this.lastStations = lastStations;
        this.sections = new SectionList();
        this.distance = 0;

        this.addBaseSection(distance);
    }

    public void updateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.name = name;
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

    public void updateColor(String color) {
        if (!StringUtils.hasText(color)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.color = color;
    }

    public List<Section> getSectionList() {
        return sections.getSections();
    }

    private void addBaseSection(Integer distance) {
        if (!sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        SectionStations stations = SectionStations.createLineBaseSection(lastStations);
        Section section = new Section(this, stations, distance);
        sections.addSection(section);
        this.distance += distance;
    }

    public LineLastStations getLastStations() {
        return lastStations;
    }

    public void addSection(Section section) {
        if (!lastStations.isLastDownwardIsSameWithSectionUpwardStation(section.getStations())) {
            throw new CustomException(ErrorCode.ONLY_DOWNWARD_CAN_BE_ADDED_TO_LINE);
        }

        sections.addSection(section);
        lastStations.updateDownLastStation(section.getDownwardStation());
        this.distance += section.getDistance();
    }

    public void deleteStation(Station targetStation) {

        if (!lastStations.isLastDownwardStation(targetStation)) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        Section removeSection = sections.removeSection(targetStation);
        lastStations.updateDownLastStation(removeSection.getUpwardStation());
        distance -= removeSection.getDistance();
    }

    public Integer getDistance() {
        return distance;
    }

    public SectionList getSections() {
        return sections;
    }
}
