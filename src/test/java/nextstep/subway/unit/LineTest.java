package nextstep.subway.unit;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.interfaces.line.dto.LineRequest;
import nextstep.subway.unit.common.LineFixture;
import nextstep.subway.unit.common.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.InvalidParameterException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    Station A역;
    Station B역;
    Station C역;
    Station D역;
    Station E역;

    Line AB라인;
    Line ABCD라인;
    Section AB구간;
    Section BC구간;
    Section CD구간;
    Section DB구간;
    Section BA구간;

    @BeforeEach
    void setUpFixture() {
        A역 = StationFixture.Entity.랜덤역생성();
        B역 = StationFixture.Entity.랜덤역생성();
        C역 = StationFixture.Entity.랜덤역생성();
        D역 = StationFixture.Entity.랜덤역생성();
        E역 = StationFixture.Entity.랜덤역생성();

        AB구간 = LineFixture.Entity.구간생성(A역,B역,2L);
        BC구간 = LineFixture.Entity.구간생성(B역,C역,3L);
        CD구간 = LineFixture.Entity.구간생성(C역,D역,4L);
        DB구간 = LineFixture.Entity.구간생성(D역,B역,5L);
        BA구간 = LineFixture.Entity.구간생성(B역,A역,5L);
    }

    // Given A-B 구간을 가지고 있는 라인에
    // When B-C 구간을 추가하면
    // Then 라인의 구간은 A-B, B-C가 되어야 한다.
    // Then 라인의 첫번째 역은 A여야 한다.
    // Then 라인의 마지막 역은 C어야 한다.
    @Test
    void addSection() {
        // Given
        AB라인 = LineFixture.Entity.라인생성("AB라인", "빨강", AB구간);

        // When
        AB라인.add(BC구간);

        // Then
        assertThat(AB라인.getSections().size()).isEqualTo(2);
        assertThat(AB라인.getSections().get(0)).isEqualTo(AB구간);
        assertThat(AB라인.getSections().get(1)).isEqualTo(BC구간);

        // Then
        assertThat(AB라인.getUpStation()).isEqualTo(A역);

        // Then
        assertThat(AB라인.getDownStation()).isEqualTo(C역);

    }

    // Given A-B 구간을 가지고 있는 라인에
    // When C-D 구간을 추가하면
    // Then 구간은 변경되면 안된다.
    // Then 잘못된 상행역 에러가 발생한다.
    @Test
    void addSectionFailWith_잘못된상행역() {
        // Given
        AB라인 = LineFixture.Entity.라인생성("AB라인", "빨강", AB구간);

        // When //Then
        Throwable exception = assertThrows(InvalidParameterException.class, () -> {
            AB라인.add(CD구간);
        });

        // Then
        assertThat(AB라인.getSections().size()).isEqualTo(1);
        assertThat(AB라인.getUpStation()).isEqualTo(A역);
        assertThat(AB라인.getDownStation()).isEqualTo(B역);

        // Then
        assertThat(exception.getMessage()).isEqualTo("잘못된 상행역");
    }

    // Given A-B 구간을 가지고 있는 라인에
    // When B-A 구간을 추가하면
    // Then 구간은 변경되면 안된다.
    // Then 이미 포함된 하행역 에러가 발생한다.
    @Test
    void addSectionFailWith_이미포함된하행역() {
        // Given
        AB라인 = LineFixture.Entity.라인생성("AB라인", "빨강", AB구간);

        // When //Then
        Throwable exception = assertThrows(CustomException.Conflict.class, () -> {
            AB라인.add(BA구간);
        });

        // Then
        assertThat(AB라인.getSections().size()).isEqualTo(1);
        assertThat(AB라인.getUpStation()).isEqualTo(A역);
        assertThat(AB라인.getDownStation()).isEqualTo(B역);

        // Then
        assertThat(exception.getMessage()).isEqualTo("이미 포함된 하행역");


    }

    // Given A-B, B-C, C-D 구간을 가지고 있는 라인에서
    // When 역 목록을 조회하면
    // Then A역 B역 C역이 순서대로 조회되어야 한다.
    @Test
    void getStations() {
        // Given
        ABCD라인 = LineFixture.Entity.라인생성("ABCD라인", "노랑", AB구간, BC구간, CD구간);

        // When
        List<Station> actual = ABCD라인.getStations();

        // Then
        assertThat(actual).isEqualTo(List.of(A역, B역, C역, D역));
    }


    // Given A-B, B-C, C-D 구간을 가지고 있는 라인에서
    // When D역이 하행역인 구간을 지우면
    // Then 라인의 구간은 A-B, B-C가 되어야 한다.
    // Then 라인의 첫번째 역은 A여야 한다.
    // Then 라인의 마지막 역은 C어야 한다.
    @Test
    void removeSection() {
        // Given
        ABCD라인 = LineFixture.Entity.라인생성("ABCD라인", "노랑", AB구간, BC구간, CD구간);

        // When
        ABCD라인.remove(D역.getId());

        // Then
        assertThat(ABCD라인.getSections().size()).isEqualTo(2);
        assertThat(ABCD라인.getSections().get(0)).isEqualTo(AB구간);
        assertThat(ABCD라인.getSections().get(1)).isEqualTo(BC구간);

        // Then
        assertThat(ABCD라인.getUpStation()).isEqualTo(A역);

        // Then
        assertThat(ABCD라인.getDownStation()).isEqualTo(C역);
    }

    // Given A-B, B-C, C-D 구간을 가지고 있는 라인에서
    // When C역이 하행역인 구간을 지우면
    // Then 구간은 변경되면 안된다.
    // Then 잘못된 하행역 에러가 발생한다.
    @Test
    void removeSectionFailWith_잘못된하행역() {
        // Given
        ABCD라인 = LineFixture.Entity.라인생성("ABCD라인", "노랑", AB구간, BC구간, CD구간);

        // When //Then
        Throwable exception = assertThrows(InvalidParameterException.class, () -> {
            ABCD라인.remove(C역.getId());
        });

        // Then
        assertThat(ABCD라인.getSections().size()).isEqualTo(3);
        assertThat(ABCD라인.getUpStation()).isEqualTo(A역);
        assertThat(ABCD라인.getDownStation()).isEqualTo(D역);

        // Then
        assertThat(exception.getMessage()).isEqualTo("잘못된 하행역");
    }

    // Given A-B 구간을 가지고 있는 라인에서
    // When B역이 하행역인 구간을 지우면
    // Then 구간은 변경되면 안된다.
    // Then 유일한 구간 에러가 발생한다.
    @Test
    void removeSectionFailWith_유일한구간() {
        // Given
        AB라인 = LineFixture.Entity.라인생성("AB라인", "빨강", AB구간);

        // When //Then
        Throwable exception = assertThrows(InvalidParameterException.class, () -> {
            AB라인.remove(B역.getId());
        });

        // Then
        assertThat(AB라인.getSections().size()).isEqualTo(1);
        assertThat(AB라인.getUpStation()).isEqualTo(A역);
        assertThat(AB라인.getDownStation()).isEqualTo(B역);

        // Then
        assertThat(exception.getMessage()).isEqualTo("유일한구간");
    }
}
