package atdd.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StationRequestView {
    private String name;

    public StationRequestView() {
    }

    @Builder
    public StationRequestView(String name) {
        this.name = name;
    }
}
