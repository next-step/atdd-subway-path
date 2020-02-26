package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;

import java.util.ArrayList;
import java.util.List;

public class LineDtoAssembler {
    public static LineResponseDto assemble(final Line line, final List<Edge> newEdges, final List<Station> stations) {
        List<StationSimpleDto> stationSimpleDtos = new ArrayList<>();

        newEdges.forEach(edgeData -> {
            if (stationSimpleDtos.isEmpty()) {
                Station sourceStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getSourceStationId()).findAny().get();
                stationSimpleDtos.add(sourceStation.toSimpleDto());

                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                stationSimpleDtos.add(targetStation.toSimpleDto());
            } else {
                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                stationSimpleDtos.add(targetStation.toSimpleDto());
            }
        });

        return LineResponseDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(stationSimpleDtos).build();
    }
}
