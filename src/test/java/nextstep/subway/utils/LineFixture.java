package nextstep.subway.utils;

import nextstep.subway.domain.Line;

import java.util.concurrent.atomic.AtomicLongArray;

public class LineFixture {

    private static final AtomicLongArray LINE_IDS = new AtomicLongArray(10);

    public static final Line 라인_생성_2호선() {
        return new Line(getId(), "2호선", "bg-green");
    }

    public static final Line 라인_생성_신분당선() {
        return new Line(getId(), "신분당선", "bg-red");
    }

    public static final Line 라인_생성_9호선() {
        return new Line(getId(), "9호선", "bg-gold");
    }

    public static final Line 라인_생성_7호선() {
        return new Line(getId(), "7호선", "bg-olive");
    }

    private static Long getId() {
        return LINE_IDS.addAndGet(1, 1);
    }

}
