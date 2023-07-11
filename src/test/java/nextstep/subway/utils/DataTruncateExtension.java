package nextstep.subway.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Component
public class DataTruncateExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        DataTruncator truncator = SpringExtension.getApplicationContext(context).getBean(DataTruncator.class);
        truncator.execute();
    }
}
