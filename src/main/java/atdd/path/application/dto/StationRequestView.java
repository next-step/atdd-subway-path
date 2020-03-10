package atdd.path.application.dto;

import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StationRequestView {
    private Long id;
    private String name;

    public StationRequestView() {
    }

    public StationRequestView(String name) {
        this.name = name;
    }

    @Builder
    public StationRequestView(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return Station.builder()
                .name(name)
                .build();
    }
}
