package atdd.path.dto;

import lombok.Getter;

@Getter
public class StationResponseView {
    private Long id;
    private String name;

    public StationResponseView(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
