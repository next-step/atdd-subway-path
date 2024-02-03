package nextstep.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ReflectionUtilsTest {

    static class FixtureWithId {
        private Long id;
    }
    static class FixtureWithoutId {
        private Long nonId;
    }


    private FixtureWithId fixture;

    @BeforeEach
    void setUp() {
        fixture = new FixtureWithId();
    }


    @Test
    @DisplayName("injectField 로 instance 에 값을 주입 할 수 있다.")
    void injectFieldTest() {
        ReflectionUtils.injectIdField(fixture, 1L);

        Assertions.assertThat(fixture.id).isEqualTo(1L);
    }

    @Test
    @DisplayName("instance 에 id field 타입이 맞지 않으면 Exception 을 던진다.")
    void injectIdFieldFailWithIdFieldTypeNotMatched() {
        assertThatThrownBy(() -> ReflectionUtils.injectIdField(fixture, "none"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("instance 에 field 중 id 이름을 가진 field 가 없으면 Exception 을 던진다.")
    void injectIdFieldFailWithIdFieldNotExist() {
        final FixtureWithoutId fixtureWithoutId = new FixtureWithoutId();
        assertThatThrownBy(() -> ReflectionUtils.injectIdField(fixtureWithoutId, 1L))
                .isInstanceOf(RuntimeException.class);
    }


}
