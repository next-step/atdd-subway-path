package nextstep.subway.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.exception.BadRequestException;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "sections")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void addSection(Long id, Station upStation, Station downStation, int distance) {
        sections.add(new Section(id, this, upStation, downStation, distance));
    }

    public List<Station> getStations(){
        return sections.getStations();
    }

    public void removeSection(Long stationId) {
        this.sections.removeSection(stationId);
    }

    public Section getSectionById(Long sectionId){
        return sections.getSectionById(sectionId);
    }

    public void modifyNameOrColor(LineRequest lineRequest){
        if(!StringUtils.hasText(lineRequest.getName()) && !StringUtils.hasText(lineRequest.getColor()))
            throw new BadRequestException("name 혹은 color를 입력하여 주십시오.");

        if (StringUtils.hasText(lineRequest.getColor())) {
            this.color = lineRequest.getColor();
        }
        if (StringUtils.hasText(lineRequest.getName())) {
            this.name = lineRequest.getName();
        }

    }


}
