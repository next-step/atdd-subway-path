package atdd;

import atdd.line.domain.Edge;
import atdd.line.domain.Line;
import atdd.station.domain.Station;
import org.assertj.core.util.Lists;

import java.time.LocalTime;

public class TestConstant {
    public static final Long STATION_ID = 1L;
    public static final Long STATION_ID_2 = 2L;
    public static final Long STATION_ID_3 = 3L;
    public static final Long STATION_ID_4 = 4L;
    public static final Long STATION_ID_5 = 5L;
    public static final Long STATION_ID_6 = 6L;
    public static final Long STATION_ID_7 = 7L;
    public static final Long STATION_ID_8 = 8L;
    public static final Long STATION_ID_9 = 9L;
    public static final Long STATION_ID_10 = 10L;
    public static final Long STATION_ID_11 = 11L;
    public static final Long STATION_ID_12 = 12L;
    public static final Long STATION_ID_13 = 13L;
    public static final Long STATION_ID_14 = 14L;
    public static final Long STATION_ID_15 = 15L;
    public static final Long STATION_ID_16 = 16L;
    public static final Long STATION_ID_17 = 17L;
    public static final Long STATION_ID_18 = 18L;
    public static final Long STATION_ID_19 = 19L;
    public static final Long STATION_ID_20 = 20L;
    public static final Long STATION_ID_21 = 21L;
    public static final Long STATION_ID_22 = 22L;

    public static final String STATION_NAME = "강남역";
    public static final String STATION_NAME_2 = "역삼역";
    public static final String STATION_NAME_3 = "선릉역";
    public static final String STATION_NAME_4 = "삼성역";
    public static final String STATION_NAME_5 = "종합운동장역";

    public static final String STATION_NAME_6 = "양재역";
    public static final String STATION_NAME_7 = "양재시민의숲역";
    public static final String STATION_NAME_8 = "청계산입구역";
    public static final String STATION_NAME_9 = "판교역";
    public static final String STATION_NAME_10 = "정자역";

    public static final String STATION_NAME_11 = "고속버스터미널역";
    public static final String STATION_NAME_12 = "교대역";
    public static final String STATION_NAME_13 = "남부터미널역";
    public static final String STATION_NAME_14 = "매봉역";
    public static final String STATION_NAME_15 = "도곡역";
    public static final String STATION_NAME_16 = "대치역";

    public static final String STATION_NAME_17 = "수서역";
    public static final String STATION_NAME_18 = "대모산입구역";
    public static final String STATION_NAME_19 = "개포동역";
    public static final String STATION_NAME_20 = "구룡역";
    public static final String STATION_NAME_21 = "한티역";
    public static final String STATION_NAME_22 = "산정릉역";

    public static final Long LINE_ID = 1L;
    public static final Long LINE_ID_2 = 2L;
    public static final Long LINE_ID_3 = 3L;
    public static final Long LINE_ID_4 = 4L;

    public static final String LINE_NAME = "2호선";
    public static final String LINE_NAME_2 = "신분당선";
    public static final String LINE_NAME_3 = "3호선";
    public static final String LINE_NAME_4 = "분당선";

    public static final Long EDGE_ID = 1L;
    public static final Long EDGE_ID_2 = 2L;
    public static final Long EDGE_ID_3 = 3L;
    public static final Long EDGE_ID_4 = 4L;
    public static final Long EDGE_ID_5 = 5L;
    public static final Long EDGE_ID_6 = 6L;
    public static final Long EDGE_ID_7 = 7L;
    public static final Long EDGE_ID_8 = 8L;
    public static final Long EDGE_ID_9 = 9L;
    public static final Long EDGE_ID_10 = 10L;
    public static final Long EDGE_ID_11 = 11L;
    public static final Long EDGE_ID_12 = 12L;
    public static final Long EDGE_ID_13 = 13L;
    public static final Long EDGE_ID_14 = 14L;
    public static final Long EDGE_ID_15 = 15L;
    public static final Long EDGE_ID_16 = 16L;
    public static final Long EDGE_ID_17 = 17L;
    public static final Long EDGE_ID_18 = 18L;
    public static final Long EDGE_ID_19 = 19L;
    public static final Long EDGE_ID_20 = 20L;
    public static final Long EDGE_ID_21 = 21L;
    public static final Long EDGE_ID_22 = 22L;
    public static final Long EDGE_ID_23 = 23L;

    public static Station TEST_STATION = Station.testBuilder().id(STATION_ID).name(STATION_NAME).build();
    public static Station TEST_STATION_2 = Station.testBuilder().id(STATION_ID_2).name(STATION_NAME_2).build();
    public static Station TEST_STATION_3 = Station.testBuilder().id(STATION_ID_3).name(STATION_NAME_3).build();
    public static Station TEST_STATION_4 = Station.testBuilder().id(STATION_ID_4).name(STATION_NAME_4).build();
    public static Station TEST_STATION_5 = Station.testBuilder().id(STATION_ID_5).name(STATION_NAME_5).build();
    public static Station TEST_STATION_6 = Station.testBuilder().id(STATION_ID_6).name(STATION_NAME_6).build();
    public static Station TEST_STATION_7 = Station.testBuilder().id(STATION_ID_7).name(STATION_NAME_7).build();
    public static Station TEST_STATION_8 = Station.testBuilder().id(STATION_ID_8).name(STATION_NAME_8).build();
    public static Station TEST_STATION_9 = Station.testBuilder().id(STATION_ID_9).name(STATION_NAME_9).build();
    public static Station TEST_STATION_10 = Station.testBuilder().id(STATION_ID_10).name(STATION_NAME_10).build();
    public static Station TEST_STATION_11 = Station.testBuilder().id(STATION_ID_11).name(STATION_NAME_11).build();
    public static Station TEST_STATION_12 = Station.testBuilder().id(STATION_ID_12).name(STATION_NAME_12).build();
    public static Station TEST_STATION_13 = Station.testBuilder().id(STATION_ID_13).name(STATION_NAME_13).build();
    public static Station TEST_STATION_14 = Station.testBuilder().id(STATION_ID_14).name(STATION_NAME_14).build();
    public static Station TEST_STATION_15 = Station.testBuilder().id(STATION_ID_15).name(STATION_NAME_15).build();
    public static Station TEST_STATION_16 = Station.testBuilder().id(STATION_ID_16).name(STATION_NAME_16).build();
    public static Station TEST_STATION_17 = Station.testBuilder().id(STATION_ID_17).name(STATION_NAME_17).build();
    public static Station TEST_STATION_18 = Station.testBuilder().id(STATION_ID_18).name(STATION_NAME_18).build();
    public static Station TEST_STATION_19 = Station.testBuilder().id(STATION_ID_19).name(STATION_NAME_19).build();
    public static Station TEST_STATION_20 = Station.testBuilder().id(STATION_ID_20).name(STATION_NAME_20).build();
    public static Station TEST_STATION_21 = Station.testBuilder().id(STATION_ID_21).name(STATION_NAME_21).build();
    public static Station TEST_STATION_22 = Station.testBuilder().id(STATION_ID_22).name(STATION_NAME_22).build();

    // 2호선
    public static Edge TEST_EDGE = Edge.testBuilder().id(EDGE_ID).sourceStation(TEST_STATION).targetStation(TEST_STATION_2).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_2 = Edge.testBuilder().id(EDGE_ID_2).sourceStation(TEST_STATION_2).targetStation(TEST_STATION_3).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_3 = Edge.testBuilder().id(EDGE_ID_3).sourceStation(TEST_STATION_3).targetStation(TEST_STATION_4).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_4 = Edge.testBuilder().id(EDGE_ID_4).sourceStation(TEST_STATION_4).targetStation(TEST_STATION_5).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_23 = Edge.testBuilder().id(EDGE_ID_23).sourceStation(TEST_STATION_12).targetStation(TEST_STATION).distance(10).elapsedTime(10).build();

    // 신분당선
    public static Edge TEST_EDGE_5 = Edge.testBuilder().id(EDGE_ID_5).sourceStation(TEST_STATION).targetStation(TEST_STATION_6).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_6 = Edge.testBuilder().id(EDGE_ID_6).sourceStation(TEST_STATION_6).targetStation(TEST_STATION_7).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_7 = Edge.testBuilder().id(EDGE_ID_7).sourceStation(TEST_STATION_7).targetStation(TEST_STATION_8).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_8 = Edge.testBuilder().id(EDGE_ID_8).sourceStation(TEST_STATION_8).targetStation(TEST_STATION_9).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_9 = Edge.testBuilder().id(EDGE_ID_9).sourceStation(TEST_STATION_9).targetStation(TEST_STATION_10).distance(10).elapsedTime(5).build();

    // 3호선
    public static Edge TEST_EDGE_10 = Edge.testBuilder().id(EDGE_ID_10).sourceStation(TEST_STATION_11).targetStation(TEST_STATION_12).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_11 = Edge.testBuilder().id(EDGE_ID_11).sourceStation(TEST_STATION_12).targetStation(TEST_STATION_13).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_12 = Edge.testBuilder().id(EDGE_ID_12).sourceStation(TEST_STATION_13).targetStation(TEST_STATION_6).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_13 = Edge.testBuilder().id(EDGE_ID_13).sourceStation(TEST_STATION_6).targetStation(TEST_STATION_14).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_14 = Edge.testBuilder().id(EDGE_ID_14).sourceStation(TEST_STATION_14).targetStation(TEST_STATION_15).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_15 = Edge.testBuilder().id(EDGE_ID_15).sourceStation(TEST_STATION_15).targetStation(TEST_STATION_16).distance(10).elapsedTime(5).build();

    // 분당선
    public static Edge TEST_EDGE_16 = Edge.testBuilder().id(EDGE_ID_16).sourceStation(TEST_STATION_17).targetStation(TEST_STATION_18).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_17 = Edge.testBuilder().id(EDGE_ID_17).sourceStation(TEST_STATION_18).targetStation(TEST_STATION_19).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_18 = Edge.testBuilder().id(EDGE_ID_18).sourceStation(TEST_STATION_19).targetStation(TEST_STATION_20).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_19 = Edge.testBuilder().id(EDGE_ID_19).sourceStation(TEST_STATION_20).targetStation(TEST_STATION_15).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_20 = Edge.testBuilder().id(EDGE_ID_20).sourceStation(TEST_STATION_15).targetStation(TEST_STATION_21).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_21 = Edge.testBuilder().id(EDGE_ID_21).sourceStation(TEST_STATION_21).targetStation(TEST_STATION_3).distance(10).elapsedTime(5).build();
    public static Edge TEST_EDGE_22 = Edge.testBuilder().id(EDGE_ID_22).sourceStation(TEST_STATION_3).targetStation(TEST_STATION_22).distance(10).elapsedTime(5).build();

    public static Line TEST_LINE = Line.testBuilder()
            .id(LINE_ID)
            .name(LINE_NAME)
            .edges(Lists.list(TEST_EDGE_23, TEST_EDGE, TEST_EDGE_2, TEST_EDGE_3, TEST_EDGE_4))
            .startTime(LocalTime.of(0, 0))
            .endTime(LocalTime.of(23, 30))
            .intervalTime(30)
            .build();

    public static Line TEST_LINE_2 = Line.testBuilder()
            .id(LINE_ID_2)
            .name(LINE_NAME_2)
            .edges(Lists.list(TEST_EDGE_5, TEST_EDGE_6, TEST_EDGE_7, TEST_EDGE_8, TEST_EDGE_9))
            .startTime(LocalTime.of(0, 0))
            .endTime(LocalTime.of(23, 30))
            .intervalTime(30)
            .build();

    public static Line TEST_LINE_3 = Line.testBuilder()
            .id(LINE_ID_3)
            .name(LINE_NAME_3)
            .edges(Lists.list(TEST_EDGE_10, TEST_EDGE_11, TEST_EDGE_12, TEST_EDGE_13, TEST_EDGE_14, TEST_EDGE_15))
            .startTime(LocalTime.of(0, 0))
            .endTime(LocalTime.of(23, 30))
            .intervalTime(30)
            .build();

    public static Line TEST_LINE_4 = Line.testBuilder()
            .id(LINE_ID_4)
            .name(LINE_NAME_4)
            .edges(Lists.list(TEST_EDGE_16, TEST_EDGE_17, TEST_EDGE_18, TEST_EDGE_19, TEST_EDGE_20, TEST_EDGE_21, TEST_EDGE_22))
            .startTime(LocalTime.of(0, 0))
            .endTime(LocalTime.of(23, 30))
            .intervalTime(30)
            .build();

}
