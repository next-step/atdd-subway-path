package nextstep.subway.line.service;

import nextstep.subway.line.repository.Line;

import java.util.List;

public interface LineFindable {
    List<Line> findAllLines();

    Line findLineById(Long id);
}
