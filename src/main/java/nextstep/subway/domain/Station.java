package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Station extends BaseEntity{
    @Column(length = 20, nullable = false)
    private String name;

    public Station(String name) {
        super();
        this.name = name;
    }
}
