package subway.acceptance.utils;

import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * query parameter 를 정의한다.
 */
@Profile("test")
public class TestParam {

    private final Map<String, String> params;

    public TestParam() {
        this.params = new HashMap<>();
    }
    public static TestParam builder() {
        return new TestParam();
    }

    /**
     * query parameter 를 추가한다.
     * @param key query parameter 의 key
     * @param value query parameter 의 value
     * @return query parameter 가 추가된 결과를 뱉는다.
     */
    public TestParam add(String key, String value) {
        params.put(key, value);
        return this;
    }

    public TestParam add(String key, int value) {
        return add(key, String.valueOf(value));
    }

    public TestParam add(String key, long value) {
        return add(key, String.valueOf(value));
    }

    public TestParam add(String key, char value) {
        return add(key, String.valueOf(value));
    }

    public TestParam add(String key, double value) {
        return add(key, String.valueOf(value));
    }

    public TestParam add(String key, float value) {
        return add(key, String.valueOf(value));
    }

    public TestParam add(String key, boolean value) {
        return add(key, String.valueOf(value));
    }


    public TestParam add(String key, BigDecimal value) {
        return add(key, String.valueOf(value));
    }
    /**
     * query parameter 를 빌드한다.
     * @return query parameter
     */
    public Map<String, String> build() {
        return params;
    }

}
