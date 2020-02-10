package atdd.station.dto.subwayLine;

import atdd.station.domain.SubwayLine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubwayLineListResponseDto {
    public List<SubwayLine> subwayLines;

    @Builder
    public SubwayLineListResponseDto(List<SubwayLine> subwayLines) {
        this.subwayLines = subwayLines;
    }

    public static SubwayLineListResponseDto toDtoEntity(List<SubwayLine> subwayLines) {
        return SubwayLineListResponseDto.builder()
                .subwayLines(subwayLines)
                .build();
    }

    public int getListDtoSize() {
        return this.subwayLines.size();
    }

    @Override
    public String toString() {
        return "SubwayLineListResponseDto{" +
                "subwayLine=" + subwayLines.get(0).getName() +
                '}';
    }
}
