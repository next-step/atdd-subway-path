package nextstep.subway.applicaion.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SectionResponse {
    private Long id;
    private Long lineId;
    public Station upStation;
    public Station downStation;
    private Integer distance;

    @Builder
    public SectionResponse(Long id, Long lineId, Station upStation, Station downStation, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static List<SectionResponse> of(List<Section> sections) {
        return sections.stream()
                .map(section -> new SectionResponse(section.getId(),
                        section.getLine().getId(),
                        section.getUpStation(),
                        section.getDownStation(),
                        section.getDistance()))
                .collect(Collectors.toList());
    }
}
