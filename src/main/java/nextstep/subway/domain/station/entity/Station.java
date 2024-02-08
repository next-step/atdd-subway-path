package nextstep.subway.domain.station.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.line.entity.Section;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@EqualsAndHashCode()
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(length = 20, nullable = false)
    @Getter
    private String name;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    private List<Section> sections;

    public Station(String name) {
        this.name = name;
    }
}
