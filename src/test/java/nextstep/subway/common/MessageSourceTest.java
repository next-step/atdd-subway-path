package nextstep.subway.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;


import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    void getMessageInKR() {
        // when
        String message = messageSource.getMessage("test", null, Locale.KOREA);

        // then
        assertThat(message).isEqualTo("테스트");
    }

    @Test
    void getMessageInEng() {
        // when
        String message = messageSource.getMessage("test", null, Locale.ENGLISH);

        // then
        assertThat(message).isEqualTo("test");
    }

    @Test
    void getMessageFailed() {
        // when
        Assertions.assertThatThrownBy(() -> messageSource.getMessage("no-message", null, null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    @Test
    void getMessageWithData() {
        // when
        String message = messageSource.getMessage("test.data", new Object[]{"데이터"}, Locale.KOREA);

        // then
        assertThat(message).isEqualTo("테스트 데이터");
    }
}
