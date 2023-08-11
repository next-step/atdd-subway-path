package nextstep.subway.applicaion.dto.path;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.StationResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PathSearchResponse {
  private List<StationResponse> stations;
  private int distance;
}
