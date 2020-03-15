package atdd.path.dto;

import lombok.Getter;

@Getter
public class StationRequestView {
    private String name;

    public StationRequestView() {
    }

    public StationRequestView(String name) {
        this.name = name;
    }
}
