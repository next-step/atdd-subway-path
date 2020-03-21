package atdd.path.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationRequestView {
    private String name;

    public StationRequestView(String name) {
        this.name = name;
    }
}
