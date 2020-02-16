package atdd.station.model.entity;


import atdd.station.converter.LineDtoListConverter;
import atdd.station.converter.LongListConverter;
import atdd.station.model.dto.LineDto;
import atdd.station.model.dto.StationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @JsonIgnore
    @Convert(converter = LongListConverter.class)
    private List<Long> lineIds = new ArrayList<>();

    @Setter
    @Transient
    @JsonProperty("lines")
    private List<LineDto> lineDtos = new ArrayList<>();

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }

    public StationDto toStationDto() {
        return StationDto.builder()
                .id(this.getId())
                .name(this.getName()).build();
    }
}