package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Station {

    public static final Station DEFAULT_STATION = new Station();

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;

    public Station(String name) {
        this.name = name;
    }
}
