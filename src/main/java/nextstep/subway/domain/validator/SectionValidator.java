package nextstep.subway.domain.validator;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.error.exception.InvalidValueException;

import static nextstep.subway.ui.error.exception.ErrorCode.*;

public class SectionValidator {

	public static void addSectionValidator(Line line, Station upStation, Station downStation) {
		if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(upStation)) {
			throw new InvalidValueException(STATION_NOT_FINAL);
		}
		for (Section section : line.getSections()) {
			if (section.getDownStation().equals(downStation) || section.getUpStation().equals(downStation)) {
				throw new InvalidValueException(ALREADY_REGISTERED_STAION);
			}
		}

	}

	public static void deleteSectionValidator(Line line, Station station) {
		if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
			throw new InvalidValueException(STATION_NOT_FINAL);
		}
		if (line.getSections().size() < 2) {
			throw new InvalidValueException(STATION_LESS_THAN_TWO);
		}
	}

	public static void checkDistance(int distance, int oldDistance) {
		if (distance >= oldDistance) {
			throw new InvalidValueException(SECTION_NOT_LONGER_THEN_EXISTING_SECTION);
		}
	}
}
