package atdd.dto;

import atdd.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StationResponseView {
    private Long id;
    private String name;

    public StationResponseView() {
    }

    @Builder
    public StationResponseView(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponseView of(Station station){
        return StationResponseView.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }
}
