package nextstep.core;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class AcceptanceTestExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        RestAssured.port = getLocalServerPort(context);
        final DatabaseCleaner databaseCleaner = getDataCleaner(context);
        databaseCleaner.clear();
    }

    private static Integer getLocalServerPort(final ExtensionContext context) {
        final Environment environment = SpringExtension.getApplicationContext(context)
                .getBean(Environment.class);
        final String port = environment.getProperty("local.server.port");
        return Integer.parseInt(port);
    }

    private DatabaseCleaner getDataCleaner(final ExtensionContext context) {
        return SpringExtension.getApplicationContext(context)
                .getBean(DatabaseCleaner.class);
    }
}
