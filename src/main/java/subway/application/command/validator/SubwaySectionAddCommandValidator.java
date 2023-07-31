package subway.application.command.validator;

import org.springframework.stereotype.Component;
import subway.application.command.in.SubwaySectionAddUsecase;
import subway.core.ValidationError;
import subway.core.ValidationErrorException;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubwaySectionAddCommandValidator {

    public void validate(SubwaySectionAddUsecase.Command command) {
        List<ValidationError> errors = new ArrayList<>();

        if (command == null) {
            errors.add(ValidationError.of("command is null"));
            throw new ValidationErrorException(errors);
        }

        validate(command, errors);
    }

    private void validate(SubwaySectionAddUsecase.Command command, List<ValidationError> errors) {
        if (command.getSubwayLineId() == null) {
            errors.add(ValidationError.of("subwayLineId", " is null"));
        }

        if (command.getSubwayLineId().getValue() == null) {
            errors.add(ValidationError.of("subwayLineId", " value is null"));
        }

        if (command.getSubwaySection() == null) {
            errors.add(ValidationError.of("subwaySection", " is null"));
            throw new ValidationErrorException(errors);
        }

        validate(command.getSubwaySection(), errors);
    }

    private void validate(SubwaySectionAddUsecase.Command.SectionCommand subwaySection, List<ValidationError> errors) {
        if (subwaySection.getUpStationId() == null) {
            errors.add(ValidationError.of("subwaySection.upStationId", " is null"));
        }

        if (subwaySection.getUpStationId().getValue() == null) {
            errors.add(ValidationError.of("subwaySection.upStationId"," value is null"));
        }

        if (subwaySection.getDownStationId() == null) {
            errors.add(ValidationError.of("subwaySection.downStationId"," is null"));
        }

        if (subwaySection.getDownStationId().getValue() == null) {
            errors.add(ValidationError.of("subwaySection.downStationId"," value is null"));
        }

        if (subwaySection.getUpStationId().equals(subwaySection.getDownStationId())) {
            errors.add(ValidationError.of("upStationId, downStationId"," are same"));
        }

        if (subwaySection.getDistance() == null) {
            errors.add(ValidationError.of("subwaySection.distance"," is null"));
            throw new ValidationErrorException(errors);
        }

        if (!subwaySection.getDistance().isPositive()) {
            errors.add(ValidationError.of("subwaySection.distance"," is less than 0"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationErrorException(errors);
        }

    }
}
