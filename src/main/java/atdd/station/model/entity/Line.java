package atdd.station.model.entity;

import atdd.station.converter.LongListConverter;
import atdd.station.model.dto.LineResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column
    @Convert(converter = LongListConverter.class)
    private List<Long> edgeIds = new ArrayList<>();

    @Transient
    private List<Edge> lineEdges = new ArrayList<>();

    @Setter
    @Transient
    private List<Station> lineStations = new ArrayList<>();

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

    public void setEdges(final List<Edge> newEdges) {
        this.edgeIds = newEdges.stream().map(Edge::getId).collect(Collectors.toList());
    }

    public List<Edge> sortEdge(final List<Edge> legacyEdges, final Edge newEdge) {
        final List<Edge> sortedEdges = new ArrayList<>();
        if (legacyEdges.isEmpty()) {
            sortedEdges.add(newEdge);

            return sortedEdges;
        }

        int i = -1;
        int deleteIndex = -1;
        boolean addNewEdge = false;
        for (Edge legacyEdge : legacyEdges) {
            i++;

            if (deleteIndex != -1 && i == deleteIndex) {
                continue;
            }

            if (!addNewEdge && legacyEdge.connectTargetStation(newEdge.getSourceStationId())) {
                sortedEdges.add(legacyEdge);
                sortedEdges.add(newEdge);

                if (i != legacyEdges.size() - 1) {
                    int elapsedTime = legacyEdge.getElapsedTime() - newEdge.getElapsedTime();
                    int distance = legacyEdge.getDistance() - newEdge.getDistance();

                    sortedEdges.add(new Edge(newEdge.getTargetStationId(), legacyEdges.get(i + 1).getTargetStationId(), elapsedTime, distance));
                    deleteIndex = i + 1;
                }

                addNewEdge = true;

                continue;
            } else if (!addNewEdge && legacyEdge.connectSourceStation(newEdge.getTargetStationId())) {
                sortedEdges.add(newEdge);
                sortedEdges.add(legacyEdge);

                addNewEdge = true;

                continue;
            }

            sortedEdges.add(legacyEdge);
        }

        return sortedEdges;
    }

    public LineResponseDto toLineResponseDto(List<Edge> edges, List<Station> stations) {
        return LineResponseDto.of(
                this,
                edges,
                stations
        );
    }
}
