package nextstep.subway.utils;


import java.util.Random;

public class RandomGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateString() {
        return generateString(3);
    }

    public static String generateString(int size) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
