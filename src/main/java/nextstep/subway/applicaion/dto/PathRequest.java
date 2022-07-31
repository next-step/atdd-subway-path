package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PathRequest {

    private Long source;
    private Long target;
}
