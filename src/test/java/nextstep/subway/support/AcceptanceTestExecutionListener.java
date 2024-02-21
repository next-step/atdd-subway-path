package nextstep.subway.support;

import io.restassured.RestAssured;
import java.util.List;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class AcceptanceTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        ServletWebServerApplicationContext context = getWebServerApplicationContext(testContext);
        RestAssured.port = getServerPort(context);
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(testContext);
        List<String> truncateQueries = getTruncateQueries(jdbcTemplate);
        truncateTables(jdbcTemplate, truncateQueries);
    }

    private int getServerPort(ServletWebServerApplicationContext context) {
        return context.getWebServer().getPort();
    }

    private ServletWebServerApplicationContext getWebServerApplicationContext(TestContext testContext) {
        org.springframework.context.ApplicationContext applicationContext = testContext.getApplicationContext();

        if (applicationContext instanceof ServletWebServerApplicationContext) {
            return (ServletWebServerApplicationContext) applicationContext;
        } else {
            throw new IllegalStateException(
                "The ApplicationContext is not an instance of ServletWebServerApplicationContext");
        }
    }

    private JdbcTemplate getJdbcTemplate(TestContext testContext) {
        return testContext.getApplicationContext().getBean(JdbcTemplate.class);
    }

    private List<String> getTruncateQueries(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(
            "SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
            String.class
        );
    }

    private void truncateTables(final JdbcTemplate jdbcTemplate, final List<String> truncateQueries) {
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(query -> execute(jdbcTemplate, query));
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void execute(final JdbcTemplate jdbcTemplate, final String query) {
        jdbcTemplate.execute(query);
    }
}
