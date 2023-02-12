package nextstep.subway.domain;

import nextstep.subway.domain.policy.section.SectionAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionBetweenDownAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionBetweenUpAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionFirstAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionLastAddPolicy;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    @Transient
    private final List<SectionAddPolicy> sectionAddPolicies
            = Arrays.asList(new SectionFirstAddPolicy(sections)
            , new SectionLastAddPolicy(sections)
            , new SectionBetweenUpAddPolicy(sections)
            , new SectionBetweenDownAddPolicy(sections));

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

    public Sections getSections() {
        return sections;
    }

    public void addSection(Section section) {
            SectionAddPolicy sectionAddPolicy = sectionAddPolicies.stream().
                    filter(policy -> policy.isSatisfied(section))
                    .findFirst()
                    .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.SECTION_CANNOT_ADD));
            sectionAddPolicy.execute(section);

    }

    public List<Station> getAllStations() {
        return sections.getStations();
    }

    public boolean equalLastStations(Station station) {
        return sections.equalLastStation(station);
    }

    public void remove(Station station) {
        sections.remove(station);
    }
}
