package subway.validation;


import subway.validation.request.PathRequest;

public abstract class PathValidator {

    private PathValidator nextValidator;

    public PathValidator(PathValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    public void validate(PathRequest request) {
        if (nextValidator != null) {
            nextValidator.validate(request);
        }
    }
}
