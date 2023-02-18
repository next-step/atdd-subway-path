package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResult;
import org.springframework.stereotype.Component;

import java.util.List;

public interface PathFind<R, T> {

    void init(List<R> paths);
    PathResult<T> find(T source, T target);
}
