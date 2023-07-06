package subway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.request.LineModifyRequest;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @ManyToOne
    @JoinColumn(name="up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne
    @JoinColumn(name="down_station_id", nullable = false)
    private Station downStation;
    private Long distance;

    @Builder
    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
    }

    public void modify(LineModifyRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }
}
