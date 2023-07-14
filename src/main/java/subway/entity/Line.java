package subway.entity;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.dto.request.LineModifyRequest;

@Getter
@Entity
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections = Sections.builder()
            .sections(Arrays.asList(section))
            .build();
    }

    public List<Station> getStations() {
        return this.sections.stations();
    }

    public void modify(LineModifyRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }
}
