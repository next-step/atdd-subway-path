package atdd.station.model.entity;

import atdd.station.converter.LongListConverter;
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

    public void addNewLineEdges(final List<Edge> legacyEdges, final Edge newEdge) {
        setEdges(sortEdge(legacyEdges, newEdge));
    }

    public void setEdges(final List<Edge> newEdges) {
        this.edgeIds = newEdges.stream().map(Edge::getId).collect(Collectors.toList());
        this.lineEdges = newEdges;
    }

    private List<Edge> sortEdge(final List<Edge> legacyEdges, final Edge newEdge) {
        final List<Edge> sortedEdges = new ArrayList<>();
        if (legacyEdges.isEmpty())
            sortedEdges.add(newEdge);

        int i = -1;
        for (Edge legacyEdge : legacyEdges) {
            i++;

            if (legacyEdge.connectTargetStation(newEdge.getSourceStationId())) {
                sortedEdges.add(legacyEdge);
                sortedEdges.add(newEdge);

                continue;
            } else if (legacyEdge.connectSourceStation(newEdge.getTargetStationId())) {
                sortedEdges.add(newEdge);
                sortedEdges.add(legacyEdge);

                continue;
            }

            if(i > 0) {
                legacyEdge.setSourceStationId(sortedEdges.get(i).getTargetStationId());
            }

            sortedEdges.add(legacyEdge);
        }

        return sortedEdges;
    }
}
