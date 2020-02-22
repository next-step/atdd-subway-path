package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;

import java.util.ArrayList;
import java.util.List;

public class LineDtoAssembler {
    public static LineDto assemble(final Line line, final List<Edge> newEdges, final List<Station> stations) {
        List<StationDto> stationDtos = new ArrayList<>();

        newEdges.forEach(edgeData -> {
            if (stationDtos.isEmpty()) {
                Station sourceStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getSourceStationId()).findAny().get();
                stationDtos.add(sourceStation.toStationDto());

                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                stationDtos.add(targetStation.toStationDto());
            } else {
                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                stationDtos.add(targetStation.toStationDto());
            }
        });

        return LineDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(stationDtos).build();
    }
}
