package subway.db.h2.mapper;

import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.common.Mapper;
import subway.domain.SubwayLine;
import subway.domain.SubwaySection;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class SubwayLineJpaMapper {

    public SubwayLineJpa from(SubwayLine subwayLine) {
        return new SubwayLineJpa(
                subwayLine.isNew() ? null : subwayLine.getId().getValue(),
                subwayLine.getName(),
                subwayLine.getColor(),
                subwayLine.getStartStationId().getValue(),
                from(subwayLine.getSections()));
    }

    private List<SubwaySectionJpa> from(List<SubwaySection> subwaySections) {
        return subwaySections.stream()
                .map(this::from)
                .collect(Collectors.toList());
    }
    private SubwaySectionJpa from(SubwaySection subwaySection) {
        return SubwaySectionJpa.register(
                subwaySection.getUpStationId().getValue(),
                subwaySection.getUpStationName(),
                subwaySection.getDownStationId().getValue(),
                subwaySection.getDownStationName(),
                subwaySection.getDistance().getValue());
    }

}
