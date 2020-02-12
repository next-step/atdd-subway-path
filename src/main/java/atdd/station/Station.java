package atdd.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Station extends AbstractEntity {

    private StationName name;

    public static Station of(String name) {
        return Station.builder()
                .name(StationName.builder()
                        .name(name)
                        .build())
                .build();
    }

    public String getName() {
        return name.getName();
    }
}
