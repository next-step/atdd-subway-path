package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.domain.dto.PathDto;

public interface Path {

    void init(final List<Line> lines);

    PathDto find(final Station source, final Station target);
}
