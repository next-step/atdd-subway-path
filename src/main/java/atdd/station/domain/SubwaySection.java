package atdd.station.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class SubwaySection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SUBWAY_LINE_ID")
    private SubwayLine subwayLine;

    @ManyToOne
    @JoinColumn(name = "SOURCE_STATION_ID")
    private Station sourceStation;

    @ManyToOne
    @JoinColumn(name = "TARGET_STATION_ID")
    private Station targetStation;

    private Integer requiredTime;

    private Double distance;

    public SubwaySection() {
    }

    private SubwaySection(SubwayLine subwayLine,
                          Station sourceStation,
                          Station targetStation) {

        this.subwayLine = subwayLine;
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
        this.distance = Double.valueOf(1);
    }

    public static SubwaySection of(SubwayLine subwayLine,
                                   Station sourceStation,
                                   Station targetStation) {

        return new SubwaySection(subwayLine, sourceStation, targetStation);
    }

    public Long getId() {
        return id;
    }

    public SubwayLine getSubwayLine() {
        return subwayLine;
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    public Double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwaySection that = (SubwaySection) o;
        return Objects.equals(subwayLine, that.subwayLine) &&
                Objects.equals(sourceStation, that.sourceStation) &&
                Objects.equals(targetStation, that.targetStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subwayLine, sourceStation, targetStation);
    }
}
