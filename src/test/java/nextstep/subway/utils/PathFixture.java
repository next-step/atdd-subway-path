package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.SectionFixture.구간생성;
import static nextstep.subway.utils.StationFixture.역생성;

public class PathFixture {

    public static Line 칠호선;
    public static Line 구호선;
    public static Line 신분당선;

    public static Station 고속터미널역;
    public static Station 반포역;
    public static Station 논현역;
    public static Station 사평역;
    public static Station 신논현역;

    public static Section 칠호선_고속터미널역_반포역;
    public static Section 칠호선_반포역_논현역;
    public static Section 신분당선_논현역_신논현역;
    public static Section 구호선_고속터미널역_신논현;

    public static void 라인을_구성한다() {
        칠호선 = 라인_생성_7호선();
        구호선 = 라인_생성_9호선();
        신분당선 = 라인_생성_신분당선();

        고속터미널역 = 역생성("고속터미널역");
        반포역 = 역생성("반포역");
        논현역 = 역생성("논현역");
        사평역 = 역생성("사평역");
        신논현역 = 역생성("신논현역");

        칠호선_고속터미널역_반포역 = 구간생성(칠호선, 고속터미널역, 반포역, 2);
        칠호선_반포역_논현역 = 구간생성(칠호선, 반포역, 논현역, 2);
        신분당선_논현역_신논현역 = 구간생성(신분당선, 논현역, 신논현역, 3);
        구호선_고속터미널역_신논현 = 구간생성(구호선, 고속터미널역, 신논현역, 10);

        칠호선.addSection(칠호선_고속터미널역_반포역);
        칠호선.addSection(칠호선_반포역_논현역);
        신분당선.addSection(신분당선_논현역_신논현역);
        구호선.addSection(구호선_고속터미널역_신논현);
    }
}
