package nextstep.subway.applicaion.dto;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
@AllArgsConstructor
public class LineStationsDto {

  private List<StationResponse> stations;
  private int totalDistance;

  public static LineStationsDto ofEmpty() {
    return new LineStationsDto(Collections.emptyList(), 0);
  }

}
