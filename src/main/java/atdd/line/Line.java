package atdd.line;

import atdd.edge.Edge;
import atdd.station.domain.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Entity
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private int intervalTime;

    @JsonIgnore
    @OneToMany(mappedBy = "line")
    private List<Edge> edges = new ArrayList();

    @Builder
    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public List<Station> getStations() {
        // 무방향 그래프 구현

        return this.edges.stream()
                .flatMap(it -> Stream.of(it.getSourceStation(), it.getTargetStation()))
                .collect(Collectors.toList());
    }
}
