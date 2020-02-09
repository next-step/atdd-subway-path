package atdd.station.domain;

import atdd.station.domain.dto.StationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder(builderMethodName = "createBuilder", builderClassName = "createBuilder")
    public Station(StationDto station) {
        this.name = station.getName();
    }
}
