package nextstep.subway.applicaion.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindPathResponse {

  List<StationResponse> stations;
  int distance;
}
