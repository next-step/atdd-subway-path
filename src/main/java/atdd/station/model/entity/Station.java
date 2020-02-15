package atdd.station.model.entity;


import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
@Getter
public class Station extends BaseEntity {
    private String name;

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }
}
