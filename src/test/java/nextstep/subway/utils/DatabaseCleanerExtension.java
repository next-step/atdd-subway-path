package nextstep.subway.utils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        SpringExtension.getApplicationContext(context)
            .getBean(DatabaseCleaner.class)
            .execute();
    }
}
