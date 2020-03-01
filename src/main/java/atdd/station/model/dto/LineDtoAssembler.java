package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineDtoAssembler {
    private Map<Long, Station> stationMap;
    private List<Edge> newEdges;

    private LineDtoAssembler(Map<Long, Station> stationMap, List<Edge> newEdges) {
        this.stationMap = stationMap;
        this.newEdges = newEdges;
    }

    private List<StationSimpleDto> getStationSimpleDtoList() {
        List<StationSimpleDto> stationSimpleDtos = new ArrayList<>();

        for (Edge newEdge : newEdges) {
            if (stationSimpleDtos.isEmpty()) {
                stationSimpleDtos.add(findStationSimpleDto(newEdge.getSourceStationId()));
                stationSimpleDtos.add(findStationSimpleDto(newEdge.getTargetStationId()));

                continue;
            }

            stationSimpleDtos.add(findStationSimpleDto(newEdge.getTargetStationId()));
        }

        return stationSimpleDtos;
    }

    private StationSimpleDto findStationSimpleDto(long stationId) {
        return stationMap.get(stationId).toSimpleDto();
    }

    public static LineResponseDto assemble(final Line line, final List<Edge> newEdges, final List<Station> stations) {
        Map<Long, Station> stationMap = stations.stream().collect(Collectors.toMap(Station::getId, station -> station));
        LineDtoAssembler assembler = new LineDtoAssembler(stationMap, newEdges);

        List<StationSimpleDto> stationSimpleDtos = assembler.getStationSimpleDtoList();

        return LineResponseDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(stationSimpleDtos).build();
    }
}
