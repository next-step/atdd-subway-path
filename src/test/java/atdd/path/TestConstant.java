package atdd.path;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
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

    public static Station TEST_STATION = new Station(STATION_ID, STATION_NAME);
    public static Station TEST_STATION_2 = new Station(STATION_ID_2, STATION_NAME_2);
    public static Station TEST_STATION_3 = new Station(STATION_ID_3, STATION_NAME_3);
    public static Station TEST_STATION_4 = new Station(STATION_ID_4, STATION_NAME_4);
    public static Station TEST_STATION_5 = new Station(STATION_ID_5, STATION_NAME_5);
    public static Station TEST_STATION_6 = new Station(STATION_ID_6, STATION_NAME_6);
    public static Station TEST_STATION_7 = new Station(STATION_ID_7, STATION_NAME_7);
    public static Station TEST_STATION_8 = new Station(STATION_ID_8, STATION_NAME_8);
    public static Station TEST_STATION_9 = new Station(STATION_ID_9, STATION_NAME_9);
    public static Station TEST_STATION_10 = new Station(STATION_ID_10, STATION_NAME_10);
    public static Station TEST_STATION_11 = new Station(STATION_ID_11, STATION_NAME_11);
    public static Station TEST_STATION_12 = new Station(STATION_ID_12, STATION_NAME_12);
    public static Station TEST_STATION_13 = new Station(STATION_ID_13, STATION_NAME_13);
    public static Station TEST_STATION_14 = new Station(STATION_ID_14, STATION_NAME_14);
    public static Station TEST_STATION_15 = new Station(STATION_ID_15, STATION_NAME_15);
    public static Station TEST_STATION_16 = new Station(STATION_ID_16, STATION_NAME_16);
    public static Station TEST_STATION_17 = new Station(STATION_ID_17, STATION_NAME_17);
    public static Station TEST_STATION_18 = new Station(STATION_ID_18, STATION_NAME_18);
    public static Station TEST_STATION_19 = new Station(STATION_ID_19, STATION_NAME_19);
    public static Station TEST_STATION_20 = new Station(STATION_ID_20, STATION_NAME_20);
    public static Station TEST_STATION_21 = new Station(STATION_ID_21, STATION_NAME_21);
    public static Station TEST_STATION_22 = new Station(STATION_ID_22, STATION_NAME_22);

    // 2호선
    public static Edge TEST_EDGE = new Edge(EDGE_ID, TEST_STATION, TEST_STATION_2, 10);
    public static Edge TEST_EDGE_2 = new Edge(EDGE_ID_2, TEST_STATION_2, TEST_STATION_3, 10);
    public static Edge TEST_EDGE_3 = new Edge(EDGE_ID_3, TEST_STATION_3, TEST_STATION_4, 10);
    public static Edge TEST_EDGE_4 = new Edge(EDGE_ID_4, TEST_STATION_4, TEST_STATION_5, 10);
    public static Edge TEST_EDGE_23 = new Edge(EDGE_ID_23, TEST_STATION_12, TEST_STATION, 10);

    // 신분당선
    public static Edge TEST_EDGE_5 = new Edge(EDGE_ID_5, TEST_STATION, TEST_STATION_6, 10);
    public static Edge TEST_EDGE_6 = new Edge(EDGE_ID_6, TEST_STATION_6, TEST_STATION_7, 20);
    public static Edge TEST_EDGE_7 = new Edge(EDGE_ID_7, TEST_STATION_7, TEST_STATION_8, 10);
    public static Edge TEST_EDGE_8 = new Edge(EDGE_ID_8, TEST_STATION_8, TEST_STATION_9, 10);
    public static Edge TEST_EDGE_9 = new Edge(EDGE_ID_9, TEST_STATION_9, TEST_STATION_10, 10);

    // 3호선
    public static Edge TEST_EDGE_10 = new Edge(EDGE_ID_10, TEST_STATION_11, TEST_STATION_12, 10);
    public static Edge TEST_EDGE_11 = new Edge(EDGE_ID_11, TEST_STATION_12, TEST_STATION_13, 10);
    public static Edge TEST_EDGE_12 = new Edge(EDGE_ID_12, TEST_STATION_13, TEST_STATION_6, 10);
    public static Edge TEST_EDGE_13 = new Edge(EDGE_ID_13, TEST_STATION_6, TEST_STATION_14, 10);
    public static Edge TEST_EDGE_14 = new Edge(EDGE_ID_14, TEST_STATION_14, TEST_STATION_15, 10);
    public static Edge TEST_EDGE_15 = new Edge(EDGE_ID_15, TEST_STATION_15, TEST_STATION_16, 10);

    // 분당선
    public static Edge TEST_EDGE_16 = new Edge(EDGE_ID_16, TEST_STATION_17, TEST_STATION_18, 10);
    public static Edge TEST_EDGE_17 = new Edge(EDGE_ID_17, TEST_STATION_18, TEST_STATION_19, 10);
    public static Edge TEST_EDGE_18 = new Edge(EDGE_ID_18, TEST_STATION_19, TEST_STATION_20, 10);
    public static Edge TEST_EDGE_19 = new Edge(EDGE_ID_19, TEST_STATION_20, TEST_STATION_15, 10);
    public static Edge TEST_EDGE_20 = new Edge(EDGE_ID_20, TEST_STATION_15, TEST_STATION_21, 10);
    public static Edge TEST_EDGE_21 = new Edge(EDGE_ID_21, TEST_STATION_21, TEST_STATION_3, 10);
    public static Edge TEST_EDGE_22 = new Edge(EDGE_ID_22, TEST_STATION_3, TEST_STATION_22, 10);

    public static Line TEST_LINE = new Line(LINE_ID, LINE_NAME, Lists.list(TEST_EDGE_23, TEST_EDGE, TEST_EDGE_2, TEST_EDGE_3, TEST_EDGE_4), LocalTime.of(5, 0), LocalTime.of(23, 30), 30);
    public static Line TEST_LINE_2 = new Line(LINE_ID_2, LINE_NAME_2, Lists.list(TEST_EDGE_5, TEST_EDGE_6, TEST_EDGE_7, TEST_EDGE_8, TEST_EDGE_9), LocalTime.of(5, 0), LocalTime.of(23, 30), 30);
    public static Line TEST_LINE_3 = new Line(LINE_ID_3, LINE_NAME_3, Lists.list(TEST_EDGE_10, TEST_EDGE_11, TEST_EDGE_12, TEST_EDGE_13, TEST_EDGE_14, TEST_EDGE_15), LocalTime.of(5, 0), LocalTime.of(23, 30), 30);
    public static Line TEST_LINE_4 = new Line(LINE_ID_4, LINE_NAME_4, Lists.list(TEST_EDGE_16, TEST_EDGE_17, TEST_EDGE_18, TEST_EDGE_19, TEST_EDGE_20, TEST_EDGE_21, TEST_EDGE_22), LocalTime.of(5, 0), LocalTime.of(23, 30), 30);
}
