package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;

import java.util.ArrayList;
import java.util.List;

public class LineDtoAssembler {
    public static LineDto assemble(final Line line, final List<Edge> newEdges, final List<Station> stations) {
        List<LineStationDto> lineStationDtos = new ArrayList<>();

        newEdges.forEach(edgeData -> {
            if (lineStationDtos.isEmpty()) {
                Station sourceStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getSourceStationId()).findAny().get();
                lineStationDtos.add(sourceStation.toLineStationDto());

                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                lineStationDtos.add(targetStation.toLineStationDto());
            } else {
                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                lineStationDtos.add(targetStation.toLineStationDto());
            }
        });

        return LineDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(lineStationDtos).build();
    }
}
