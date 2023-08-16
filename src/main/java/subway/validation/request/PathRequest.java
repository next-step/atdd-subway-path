package subway.validation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PathRequest {

    private Long source;
    private Long target;

}
