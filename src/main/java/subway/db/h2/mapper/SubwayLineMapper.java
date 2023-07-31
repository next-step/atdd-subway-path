package subway.db.h2.mapper;

import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.common.Mapper;
import subway.domain.*;

import java.util.Objects;
import java.util.stream.Collectors;

@Mapper
public class SubwayLineMapper {

    public SubwayLine from(SubwayLineJpa subwayLineJpa) {
        return SubwayLine.of(
                new SubwayLine.Id(Objects.requireNonNull(subwayLineJpa.getId())),
                subwayLineJpa.getName(),
                subwayLineJpa.getColor(),
                new Station.Id(subwayLineJpa.getStartStationId()),
                subwayLineJpa.getSubwaySections()
                        .stream()
                        .map(this::from)
                        .collect(Collectors.toList()));
    }

    private SubwaySection from(SubwaySectionJpa sectionJpa) {
        return SubwaySection.of(
                new SubwaySection.Id(Objects.requireNonNull(sectionJpa.getId())),
                new SubwaySectionStation(new Station.Id(sectionJpa.getUpStationId()), sectionJpa.getUpStationName()),
                new SubwaySectionStation(new Station.Id(sectionJpa.getDownStationId()), sectionJpa.getDownStationName()),
                Kilometer.of(sectionJpa.getDistance()));
    }
}
