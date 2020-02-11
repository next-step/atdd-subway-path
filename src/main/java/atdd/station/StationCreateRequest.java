package atdd.station;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StationCreateRequest {
    private String name;

    @Builder
    public StationCreateRequest(String name){
        this.name = name;
    }
    public Station toEntity() {
        return Station.builder()
                .name(name)
                .build();
    }
}
