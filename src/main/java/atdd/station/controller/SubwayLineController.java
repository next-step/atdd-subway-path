package atdd.station.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/subway-lines",
        produces = "application/json")
public class SubwayLineController {

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody String subwayLine) {
        return ResponseEntity
                .created(URI.create("/subway-lines/1"))
                .body("{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"2호선\",\n" +
                "    \"startTime\": \"05:00\",\n" +
                "    \"endTime\": \"23:50\",\n" +
                "    \"interval\": \"10\",\n" +
                "    \"stations\": [\n" +
                "        {\"id\":4,\"name\":\"교대역\"},\n" +
                "        {\"id\":5,\"name\":\"강남역\"},\n" +
                "        {\"id\":2,\"name\":\"역삼역\"},\n" +
                "        {\"id\":3,\"name\":\"선릉역\"},\n" +
                "        {\"id\":1,\"name\":\"삼성역\"}\n" +
                "    ]\n" +
                " }");
    }
}
