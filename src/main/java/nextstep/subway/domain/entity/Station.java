package nextstep.subway.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.response.StationResponse;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    public Station(String name) {
        this.name = name;
    }

    public StationResponse createStationResponse() {
        return new StationResponse(
                this.id,
                this.name
        );
    }
}
