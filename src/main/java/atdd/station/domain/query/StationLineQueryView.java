package atdd.station.domain.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StationLineQueryView {

    private Long id;
    private String name;

    public StationLineQueryView(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
