package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isMatchId(Long stationId) {
        return id.equals(stationId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
