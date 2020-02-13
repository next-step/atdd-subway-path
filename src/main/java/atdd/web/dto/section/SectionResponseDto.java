package atdd.web.dto.section;

import atdd.domain.stations.Line;
import atdd.domain.stations.Section;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SectionResponseDto {
    private Long id;
    private Line line;
    private Stations source;
    private Stations target;

    @Builder
    public SectionResponseDto(Long id, Line line, Stations source, Stations target) {
        this.id = id;
        this.line = line;
        this.source = source;
        this.target = target;
    }

    public SectionResponseDto toRealDto(Section section) {
        return SectionResponseDto.builder()
                .line(section.getLine())
                .source(section.getSource())
                .target(section.getTarget())
                .build();
    }
}
