package atdd.station.model.entity;

import atdd.station.converter.LongListConverter;
import atdd.station.converter.StationDtoConverter;
import atdd.station.model.dto.StationDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Table
@Entity
@Getter
@Setter
public class Line extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Column(nullable = false)
    private int intervalTime;

    @JsonIgnore
    @Convert(converter = LongListConverter.class)
    @Column
    private List<Long> edgeIds = new ArrayList<>();

    @Convert(converter = StationDtoConverter.class)
    @Transient
    @JsonProperty("stations")
    private List<StationDto> stationDtos = new ArrayList<>();

    public Line() {
    }

    @Builder
    private Line(final String name,
                 final LocalTime startTime,
                 final LocalTime endTime,
                 final int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public void updateEdge(final List<Edge> newEdges, final List<Station> stations) {
        setEdges(newEdges);
        setStationDtos(newEdges, stations);
    }

    private void setEdges(final List<Edge> newEdges) {
        this.edgeIds = newEdges.stream().map(Edge::getId).collect(Collectors.toList());
    }

    public void setStationDtos(final List<Edge> newEdges, final List<Station> stations) {
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
    }
}
