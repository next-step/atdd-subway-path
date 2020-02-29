package atdd.web.dto.section;

import atdd.domain.stations.Section;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SectionCreateRequestDto {
    private Stations source;
    private Stations target;

    @Builder
    public SectionCreateRequestDto(Stations source, Stations target) {
        this.source = source;
        this.target = target;
    }

    public Section toEntity() {
        return Section.builder()
                .source(source)
                .target(target)
                .build();
    }
}
