package nextstep.subway.fake;

public class SubwayExceptionMessage {


    public static String 중복된_구간을_등록했을_경우() {
        return "상행역과 하행역이 모두 등록 되어있는 구간은 등록할 수 없습니다.";
    }

    public static String 아무런_역도_포함되지_않은_경우() {
        return "상행역이나 하행역 중 하나는 등록된 구간에 포함되어야 합니다.";
    }

    public static String 기존_거리보다_길거나_같은_구간을_등록할_경우() {
        return "역 사이에 등록할 경우 기존의 거리보다 짧은 구간만 등록이 가능합니다.";
    }
}
