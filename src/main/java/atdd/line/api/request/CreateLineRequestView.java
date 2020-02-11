package atdd.line.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CreateLineRequestView {

    private String name;
    private String startTime;
    private String endTime;
    private int intervalTime;

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateLineRequestView.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("startTime='" + startTime + "'")
                .add("endTime='" + endTime + "'")
                .add("intervalTime=" + intervalTime)
                .toString();
    }

}
