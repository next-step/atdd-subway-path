package nextstep.subway.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nextstep.subway.utils.AcceptanceMethods.makeStation;


public class StationFixtures {
    private static final String[] NAMES = {"강남역", "역삼역", "삼성역", "판교역"};

    public static List<Long> stationFixtures(int number) {
        if (number > NAMES.length){
            throw new RuntimeException("생성하려는 역의 갯수가 지정된 이름의 갯수보다 많습니다.");
        }

        return IntStream.range(0, number)
                .mapToObj(i -> makeStation(NAMES[i]).jsonPath().getLong("id"))
                .collect(Collectors.toList());
    }
}
