package atdd.line.api.request;

import atdd.global.util.LocalTimeUtils;
import atdd.line.domain.Line;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CreateLineRequestView {

    private String name;
    private String startTime;
    private String endTime;
    private int intervalTime;

    public Line toLine() {
        return Line.builder()
                .name(name)
                .startTime(LocalTimeUtils.valueOf(startTime))
                .endTime(LocalTimeUtils.valueOf(endTime))
                .intervalTime(intervalTime)
                .build();
    }

}
