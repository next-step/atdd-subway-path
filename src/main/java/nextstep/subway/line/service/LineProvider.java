package nextstep.subway.line.service;

import nextstep.subway.line.domain.Line;

import java.util.List;

public interface LineProvider {
    List<Line> getAllLines();
}
