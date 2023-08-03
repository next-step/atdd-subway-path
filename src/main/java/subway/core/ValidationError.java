package subway.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationError {
    private String field;
    private String message;

    public ValidationError(String field, String message) {
        this.message = message;
        this.field = field;
    }

    public boolean hasName() {
        return message != null;
    }

    public static ValidationError of(String field) {
        return new ValidationError(field, null);
    }

    public static ValidationError of(String field, String message) {
        return new ValidationError(field, message);
    }
}
