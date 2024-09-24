package nextstep.subway.utils;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jackson.introspector.JacksonObjectArbitraryIntrospector;
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;

import java.util.List;

public class FixtureUtil {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(JacksonObjectArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .plugin(new JacksonPlugin())
            .plugin(new JavaxValidationPlugin())
            .build();

    public static <T> T getFixture(Class<T> clazz) {
        return fixtureMonkey.giveMeOne(clazz);
    }

    public static <T> List<T> getFixtures(Class<T> clazz, int count) {
        return fixtureMonkey.giveMe(clazz, count);
    }

    public static <T> ArbitraryBuilder<T> getBuilder(Class<T> clazz) {
        return fixtureMonkey.giveMeBuilder(clazz);
    }
}
