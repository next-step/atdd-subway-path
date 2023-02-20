package nextstep.subway.domain.exception;

public class SectionExceptionMessages {

    public static final String INVALID_DISTANCE = "유효하지 않은 구간 길이입니다.";
    public static final String ALREADY_EXIST = "이미 존재하는 구간입니다.";
    public static final String NOTHING_EXIST = "상, 하행역 모두 해당 노선에서 찾을 수 없습니다.";
    public static final String CANNOT_REMOVE_SECTION_WHEN_LINE_HAS_ONLY_ONE = "노선에 구간이 한 개인 경우, 해당 구간은 제거할 수 없습니다.";
    public static final String CANNOT_FIND_SECTION = "구간을 찾을 수 없습니다.";

}
