package subway.validation.impl;

import subway.exception.impl.CannotFindPath;
import subway.validation.PathValidator;
import subway.validation.request.PathRequest;

public class PathSourceTargetEqualsValidator extends PathValidator {

    public PathSourceTargetEqualsValidator(PathValidator nextValidator) {
        super(nextValidator);
    }

    @Override
    public void validate(PathRequest request) {
        if (request.getSource().equals(request.getTarget())) {
            throw new CannotFindPath();
        }

        super.validate(request);
    }
}
