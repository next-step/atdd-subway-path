package subway.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class PathRequest {

    @NotNull(message = "출발역은 반드시 입력해야합니다.")
    private Long source;

    @NotNull(message = "도착역은 반드시 입력해야합니다.")
    private Long target;

}
