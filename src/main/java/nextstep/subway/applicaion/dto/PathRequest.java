package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.exception.path.InvalidPathException;
import nextstep.subway.domain.exception.path.SourceAndTargetCannotBeSameException;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class PathRequest {

    private final Long source;
    private final Long target;

    public void validate() {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new InvalidPathException();
        }

        if (source.equals(target)) {
            throw new SourceAndTargetCannotBeSameException();
        }
    }
}
