package subway.db.h2.mapper;

import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.common.Mapper;
import subway.application.query.response.SubwayLineResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper
public class SubwayLineResponseMapper {

    public SubwayLineResponse from(SubwayLineJpa subwayLineJpa) {
        return new SubwayLineResponse(
                subwayLineJpa.getId(),
                subwayLineJpa.getName(),
                subwayLineJpa.getColor(),
                mapStationsFrom(subwayLineJpa));
    }

    private List<SubwayLineResponse.StationInfo> mapStationsFrom(SubwayLineJpa subwayLineJpa) {
        Map<Long, SubwaySectionJpa> idToSectionMap = mapIdToSectionMapFrom(subwayLineJpa);

        List<SubwayLineResponse.StationInfo> orderedStations = new ArrayList<>();

        SubwaySectionJpa subwaySectionJpa = idToSectionMap.get(subwayLineJpa.getStartStationId());
        SubwayLineResponse.StationInfo startStationInfo = mapStationBy(subwaySectionJpa.getUpStationId(), subwaySectionJpa.getUpStationName());
        orderedStations.add(startStationInfo);
        Long upStationId = subwaySectionJpa.getUpStationId();

        while (idToSectionMap.containsKey(upStationId)) {
            subwaySectionJpa = idToSectionMap.get(upStationId);
            orderedStations.add(mapDownStationInfoFrom(subwaySectionJpa));
            upStationId = subwaySectionJpa.getDownStationId();
        }

        return orderedStations;
    }

    private static Map<Long, SubwaySectionJpa> mapIdToSectionMapFrom(SubwayLineJpa subwayLineJpa) {
        return subwayLineJpa.getSubwaySections().stream().collect(Collectors.toMap(SubwaySectionJpa::getUpStationId, Function.identity()));
    }

    private SubwayLineResponse.StationInfo mapDownStationInfoFrom(SubwaySectionJpa subwaySectionJpa) {
        return mapStationBy(subwaySectionJpa.getDownStationId(), subwaySectionJpa.getDownStationName());
    }

    private SubwayLineResponse.StationInfo mapStationBy(Long id, String name) {
        return new SubwayLineResponse.StationInfo(id, name);
    }
}
