package atdd.line.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class LinesResponseView {

    private int count;
    private List<LineResponseView> lines;

    public LinesResponseView(List<LineResponseView> lines) {
        this.count = lines.size();
        this.lines = lines;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LineResponseView.class.getSimpleName() + "[", "]")
                .add("count=" + count)
                .add("lines=" + lines)
                .toString();
    }

}
