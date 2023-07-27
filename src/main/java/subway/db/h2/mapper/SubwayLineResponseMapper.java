package subway.db.h2.mapper;

import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.common.Mapper;
import subway.application.query.response.SubwayLineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class SubwayLineResponseMapper {

    public SubwayLineResponse from(SubwayLineJpa subwayLineJpa) {
        return new SubwayLineResponse(
                subwayLineJpa.getId(),
                subwayLineJpa.getName(),
                subwayLineJpa.getColor(),
                subwayLineJpa
                        .getSubwaySections()
                        .stream()
                        .flatMap(section -> from(section).stream()).collect(Collectors.toList()));
    }

    private List<SubwayLineResponse.StationInfo> from(SubwaySectionJpa subwaySectionJpa) {
        return List.of(from(subwaySectionJpa.getUpStationId(), subwaySectionJpa.getUpStationName()),
                from(subwaySectionJpa.getDownStationId(), subwaySectionJpa.getDownStationName()));
    }

    private SubwayLineResponse.StationInfo from(Long id, String name) {
        return new SubwayLineResponse.StationInfo(id, name);
    }
}
