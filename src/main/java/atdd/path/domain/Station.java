package atdd.path.domain;

import atdd.path.dto.StationRequestView;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;

@Getter
@Entity
public class Station {
    private Long id;
    private String name;

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Station of(StationRequestView requestView){
        return Station.builder()
                .name(requestView.getName())
                .build();
    }
}
