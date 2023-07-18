package nextstep.subway.utils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Component
public class DataTruncateExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        DataTruncator truncator = SpringExtension.getApplicationContext(context).getBean(DataTruncator.class);
        truncator.execute();
    }
}
