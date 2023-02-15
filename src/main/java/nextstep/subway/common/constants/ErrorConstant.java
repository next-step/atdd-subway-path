package nextstep.subway.common.constants;

public class ErrorConstant {

    public static final String MORE_THEN_DISTANCE = "기존 구간 사이에 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.";
    public static final String ALREADY_ENROLL_STATION = "상행 역과 하행 역이 이미 노선에 모두 등록되어 있다면 등록할 수 없습니다.";
    public static final String NOT_ENROLL_STATION = "상행 역과 하행 역 둘 중 하나도 포함되어 있지 않으면 등록할 수 없습니다.";
    public static final String LESS_THAN_ONE_SECTION = "노선에 등록된 구간이 하나 이하일 경우 구간을 제거할 수 없습니다.";
    public static final String NOT_FOUND_STATION = "역을 찾을 수 없습니다.";
}
