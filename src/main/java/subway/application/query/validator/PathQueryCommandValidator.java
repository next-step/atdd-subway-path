package subway.application.query.validator;

import org.springframework.stereotype.Component;
import subway.application.query.in.PathQuery;
import subway.core.ValidationError;
import subway.core.ValidationErrorException;

import java.util.ArrayList;
import java.util.List;

@Component
public class PathQueryCommandValidator {

    public void validate(PathQuery.Command command) {
        List<ValidationError> errors = new ArrayList<>();

        if (command == null) {
            errors.add(ValidationError.of("command is null"));
            throw new ValidationErrorException(errors);
        }

        validate(command, errors);
    }

    private void validate(PathQuery.Command command, List<ValidationError> errors) {
        if (command.getStartStationId() == null) {
            errors.add(ValidationError.of("startStationId", " is null"));
            throw new ValidationErrorException(errors);
        }

        if (command.getStartStationId().isEmpty()) {
            errors.add(ValidationError.of("startStationId", " value is null"));
            throw new ValidationErrorException(errors);
        }

        if (command.getEndStationId() == null) {
            errors.add(ValidationError.of("endStationId", " is null"));
            throw new ValidationErrorException(errors);
        }

        if (command.getEndStationId().isEmpty()) {
            errors.add(ValidationError.of("endStationId", " value is null"));
            throw new ValidationErrorException(errors);
        }

        if (command.getStartStationId().equals(command.getEndStationId())) {
            errors.add(ValidationError.of("startStationId, endStationId", " are same"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationErrorException(errors);
        }
    }
}
