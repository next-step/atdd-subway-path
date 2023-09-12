package subway.application.command.validator;

import org.springframework.stereotype.Component;
import subway.application.command.in.SubwaySectionCloseUsecase;
import subway.core.ValidationError;
import subway.core.ValidationErrorException;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubwaySectionCloseCommandValidator {

    public void validate(SubwaySectionCloseUsecase.Command command) {
        List<ValidationError> errors = new ArrayList<>();

        if (command == null) {
            errors.add(ValidationError.of("command is null"));
            throw new ValidationErrorException(errors);
        }

        validate(command, errors);
    }

    private void validate(SubwaySectionCloseUsecase.Command command, List<ValidationError> errors) {
        if (command.getSubwayLineId() == null) {
            errors.add(ValidationError.of("subwayLineId", " is null"));
        }

        if (command.getSubwayLineId() != null && command.getSubwayLineId().isEmpty()) {
            errors.add(ValidationError.of("subwayLineId", " value is null"));
        }

        if (command.getSection() == null) {
            errors.add(ValidationError.of("section", " is null"));
            throw new ValidationErrorException(errors);
        }

        validate(command.getSection(), errors);
    }

    private void validate(SubwaySectionCloseUsecase.Command.SectionCommand subwaySection, List<ValidationError> errors) {
        if (subwaySection.getStationId() == null) {
            errors.add(ValidationError.of("section.stationId", " is null"));
        }

        if (subwaySection.getStationId() != null && subwaySection.getStationId().isEmpty()) {
            errors.add(ValidationError.of("section.stationId"," value is null"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationErrorException(errors);
        }

    }
}
