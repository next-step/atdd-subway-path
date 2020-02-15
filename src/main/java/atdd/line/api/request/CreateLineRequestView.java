package atdd.line.api.request;

import atdd.line.domain.Line;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.StringJoiner;

import static atdd.global.util.LocalTimeUtils.localTimeOf;
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
                .startTime(getLocalTime(startTime))
                .endTime(getLocalTime(endTime))
                .intervalTime(intervalTime)
                .build();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateLineRequestView.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("startTime='" + startTime + "'")
                .add("endTime='" + endTime + "'")
                .add("intervalTime=" + intervalTime)
                .toString();
    }

    private LocalTime getLocalTime(String localTime) {
        return localTimeOf(localTime).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파라미터 입니다."));
    }

}
