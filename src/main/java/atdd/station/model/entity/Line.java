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

    @Setter
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

    public void updateEdge(final List<Edge> newEdges, final List<Station> lineStations) {
        this.edgeIds = newEdges.stream().map(Edge::getId).collect(Collectors.toList());
        this.lineEdges = newEdges;
        this.lineStations = lineStations;
    }
}
