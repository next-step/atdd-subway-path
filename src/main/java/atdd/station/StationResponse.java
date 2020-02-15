package atdd.station;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(){
    }

    @Builder
    public StationResponse(Station entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public static StationResponse of(Station entity){
        return StationResponse.builder()
                .entity(entity)
                .build();
    }
}
