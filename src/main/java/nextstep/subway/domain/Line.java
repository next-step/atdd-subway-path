package nextstep.subway.domain;

import nextstep.subway.domain.policy.section.SectionAddPolicies;
import nextstep.subway.domain.policy.section.SectionAddPolicy;
import nextstep.subway.domain.policy.section.StationRemovePolices;
import nextstep.subway.domain.policy.section.StationRemovePolicy;

import javax.persistence.*;
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
    private final SectionAddPolicies sectionAddPolicies = new SectionAddPolicies();
    @Transient
    private final StationRemovePolices stationRemovePolicies = new StationRemovePolices();

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
        SectionAddPolicy policy = sectionAddPolicies.getSuitable(this.sections, section);
        policy.execute(this.sections, section);
    }

    public List<Station> getAllStations() {
        return sections.getStations();
    }

    public boolean equalLastStations(Station station) {
        return sections.equalLastStation(station);
    }

    public void remove(Station station) {
        StationRemovePolicy policy = stationRemovePolicies.isSuitable(this.sections, station);
        policy.execute(sections, station);
    }
}
