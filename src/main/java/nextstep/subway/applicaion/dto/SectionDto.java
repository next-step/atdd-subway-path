package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SectionDto {
    private Long id;

    private Line line;

    private Station upStation;

    private Station downStation;

    private int distance;

    public static SectionDto from(Section section) {
        return new SectionDto(section.getId(), section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistanceIntValue());
    }

    public static List<Section> toEntity(List<SectionDto> sections) {
        return sections.stream().map(SectionDto::toEntity).collect(Collectors.toList());
    }

    public static Section toEntity(SectionDto section) {
        return new Section(section.getId(), section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
