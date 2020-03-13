package atdd.domain;

import atdd.dto.StationRequestView;
import atdd.dto.StationResponseView;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class Station {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station() {
    }

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

    public static Station of(StationResponseView responseView){
        return Station.builder()
                .id(responseView.getId())
                .name(responseView.getName())
                .build();
    }
}
