package atdd.path.dto;

import lombok.Getter;

@Getter
public class StationRequestView {
    private String name;

    public StationRequestView(String name) {
        this.name = name;
    }
}
