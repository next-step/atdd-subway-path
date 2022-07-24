package nextstep.subway.domain;

import java.util.Arrays;

public enum KindOfAddition {
	ADD_TO_IN_EXISTS_SECTION("기존 구간 사이에 추가"),
	ADD_TO_TOP_OF_UP_STATION("상행 종점에 추가"),
	ADD_TO_END_OF_DOWN_STATION("하행 종점에 추가");

	private String description;

	KindOfAddition(String description) {
		this.description = description;
	}

	public boolean isSimpleIndex() {
		return Arrays.asList(ADD_TO_TOP_OF_UP_STATION, ADD_TO_END_OF_DOWN_STATION).contains(this);
	}

}
