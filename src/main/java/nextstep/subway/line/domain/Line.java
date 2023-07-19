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

    @Embedded
    private SectionList sections;

    protected Line() {}

    public Line(Long id, String name, String color, SectionList sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Section section) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(color)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.name = name;
        this.color = color;
        this.sections = new SectionList();

        this.addSection(section);
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

    public void addSection(Section section) {
        sections.addSection(section);
        section.updateLine(this);
    }

    public void deleteStation(Station targetStation) {
        sections.removeSection(targetStation);
    }

    public Integer getDistance() {
        return sections.getDistance();
    }

    public SectionList getSections() {
        return sections;
    }

    public Station getUpLastStation() {
        return sections.getUpLastStation();
    }

    public Station getDownLastStation() {
        return sections.getDownLastStation();
    }
}
