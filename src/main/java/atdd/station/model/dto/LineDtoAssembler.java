package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;

import java.util.ArrayList;
import java.util.List;

public class LineDtoAssembler {
    public static LineDto assemble(final Line line, final List<Edge> newEdges, final List<Station> stations) {
        List<IdNameDto> idNameDtos = new ArrayList<>();

        newEdges.forEach(edgeData -> {
            if (idNameDtos.isEmpty()) {
                Station sourceStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getSourceStationId()).findAny().get();
                idNameDtos.add(sourceStation.toIdNameDto());

                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                idNameDtos.add(targetStation.toIdNameDto());
            } else {
                Station targetStation = stations.stream().filter(stationData -> stationData.getId() == edgeData.getTargetStationId()).findAny().get();
                idNameDtos.add(targetStation.toIdNameDto());
            }
        });

        return LineDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(idNameDtos).build();
    }
}
