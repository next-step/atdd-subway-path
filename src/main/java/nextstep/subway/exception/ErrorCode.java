package nextstep.subway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	CANNOT_INSERT_LONGER_SECTION(HttpStatus.BAD_REQUEST, "기존 구간보다 길이가 더 긴 구간은 삽입할 수 없습니다."),
	CANNOT_INSERT_SAME_DISTANCE_SECTION(HttpStatus.BAD_REQUEST, "기존 구간보다 길이가 같은 구간은 삽입할 수 없습니다."),
	CANNOT_REGISTER_ALREADY_REGISTERED_SECTION(HttpStatus.BAD_REQUEST, "상행역과 하행역이 이미 등록되어 있으면, 구간을 추가할 수 없습니다."),
	CANNOT_REGISTER_WITHOUT_REGISTERED_STATIONS(HttpStatus.BAD_REQUEST, "상행역과 하행역이 모두 등록되어 있지 않은 구간은 등록할 수 없습니다.");

	private HttpStatus status;
	private String message;
}
