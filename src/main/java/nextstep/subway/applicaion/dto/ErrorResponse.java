package nextstep.subway.applicaion.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final String message;

	public ErrorResponse(String message){
		this.message = message;
	}
}
