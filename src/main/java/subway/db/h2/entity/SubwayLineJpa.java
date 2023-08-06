package subway.db.h2.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Station;
import subway.domain.SubwayLine;
import subway.domain.SubwaySection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "subway_lines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public SubwayLineJpa(Long id, String name, String color, Long startStationId, List<SubwaySectionJpa> subwaySections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.subwaySections = subwaySections;
    }

    public void update(SubwayLine subwayLine) {
        update(subwayLine.getName(), subwayLine.getColor());
        updateSections(subwayLine);
        addSections(subwayLine);
        deleteSections(subwayLine);
    }

    private void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private void updateSections(SubwayLine subwayLine) {
        for (SubwaySectionJpa subwaySectionJpa : subwaySections) {
            Station.Id id = new Station.Id(subwaySectionJpa.getUpStationId());
            subwayLine.getSection(id)
                    .ifPresent(subwaySection ->
                            subwaySectionJpa.update(
                                    subwaySection.getUpStationId().getValue(),
                                    subwaySection.getUpStationName(),
                                    subwaySection.getDownStationId().getValue(),
                                    subwaySection.getDownStationName(),
                                    subwaySection.getDistance().getValue()));
        }
    }

    private void addSections(SubwayLine subwayLine) {

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


    private void deleteSections(SubwayLine subwayLine) {
        subwaySections
                .removeIf(subwaySectionJpa ->
                        !subwayLine.existsUpStation(new Station.Id(subwaySectionJpa.getUpStationId())));
    }
}
