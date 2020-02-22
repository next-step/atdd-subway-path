package atdd.station.model.entity;


import atdd.station.converter.LongListConverter;
import atdd.station.model.dto.LineStationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
public class Station extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Setter
    @Column
    @Convert(converter = LongListConverter.class)
    private List<Long> lineIds = new ArrayList<>();

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }

    public LineStationDto toLineStationDto() {
        return LineStationDto.builder()
                .id(this.getId())
                .name(this.getName()).build();
    }
}