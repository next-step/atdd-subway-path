package nextstep.data;

import java.util.HashMap;
import java.util.Map;

public class Lines {
    public static final Map<String, String> 사호선 = new HashMap<String, String>(){{
       put("name", "사호선");
       put("color", "bg-skyblue-600");
    }};
    public static final Map<String, String> 이호선 = new HashMap<String, String>(){{
        put("name", "이호선");
        put("color", "bg-green-600");
    }};
    public static final Map<String, String> 신분당선 = new HashMap<String, String>(){{
        put("name", "신분당선");
        put("color", "bg-red-600");
    }};
}
