package subway.db.h2.entity;

import subway.domain.Station;
import subway.domain.SubwayLine;
import subway.domain.SubwaySection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subway_lines")
public class SubwayLineJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subway_line_id")
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;

    @Column(nullable = false)
    private Long startStationId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "subway_line_id")
    private List<SubwaySectionJpa> subwaySections = new ArrayList<>();

    public SubwayLineJpa() {
    }

    public SubwayLineJpa(Long id, String name, String color, Long startStationId, List<SubwaySectionJpa> subwaySections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.subwaySections = subwaySections;
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

    public Long getStartStationId() {
        return startStationId;
    }

    public List<SubwaySectionJpa> getSubwaySections() {
        return subwaySections;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isNew() {
        return id == null;
    }

    public void updateSections(SubwayLine subwayLine) {
        for (SubwaySectionJpa subwaySectionJpa : subwaySections) {
            Station.Id id = new Station.Id(subwaySectionJpa.getUpStationId());
            if (subwayLine.existsUpStation(id)) {
                SubwaySection section = subwayLine.getSection(id);
                subwaySectionJpa.update(
                        section.getUpStationId().getValue(),
                        section.getUpStationName(),
                        section.getDownStationId().getValue(),
                        section.getDownStationName(),
                        section.getDistance().getValue());
            }
        }
    }

    public void addSections(SubwayLine subwayLine) {

        subwayLine.getSections()
                .stream()
                .filter(SubwaySection::isNew)
                .map(subwaySection ->
                        new SubwaySectionJpa(
                                subwaySection.getUpStationId().getValue(),
                                subwaySection.getUpStationName(),
                                subwaySection.getDownStationId().getValue(),
                                subwaySection.getDownStationName(),
                                subwaySection.getDistance().getValue()))
                .forEach(subwaySection ->
                        subwaySections.add(subwaySection));
    }


    public void deleteSections(SubwayLine subwayLine) {
        subwaySections
                .removeIf(subwaySectionJpa ->
                        !subwayLine.existsUpStation(new Station.Id(subwaySectionJpa.getUpStationId())));
    }
}
