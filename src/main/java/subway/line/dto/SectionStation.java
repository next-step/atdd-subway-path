package subway.line.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.station.model.Station;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionStation {
    private Station upStation;
    private Station downStation;
}
