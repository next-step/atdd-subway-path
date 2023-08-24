package subway.application.query.out;

import subway.domain.PathSearcher;

import java.util.Optional;

public interface PathSearcherLoadPort {

    Optional<PathSearcher> findOne();
}
