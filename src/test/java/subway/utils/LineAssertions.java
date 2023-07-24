package subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;

public class LineAssertions {

    public static void 구간추가후_검증(Line 노선) {
        assertThat(노선.getDistance()).isEqualTo(40L);

        Sections 구간리스트 = 노선.getSections();
        Section 마지막구간 = 노선.getSections().getLastSection();
        assertThat(구간리스트.getSize()).isEqualTo(2);
        assertThat(구간리스트.getAllStations().size()).isEqualTo(3);
        assertThat(마지막구간.getUpStation().getName()).isEqualTo("논현역");
        assertThat(마지막구간.getDownStation().getName()).isEqualTo("광교역");

        Assertions.assertThat(노선.getSections().getLastStation().getName()).isEqualTo("광교역");
        Assertions.assertThat(노선.getDistance()).isEqualTo(40L);
    }

}
