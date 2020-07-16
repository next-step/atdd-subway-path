package nextstep.subway.line.dto;

import java.util.List;
import java.util.Objects;

public class LineResponses {

    private List<LineResponse> lineResponses;

    protected LineResponses() {
    }

    public LineResponses(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }

    public LineResponse getLineResponseByLineId(Long lineId) {
        return lineResponses.stream()
            .filter(it -> Objects.equals(it.getId(), lineId))
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }
}
