/*
 *
 * StationController
 *
 * 0.0.1
 *
 * Copyright 2020 irrationnelle <drakkarverenis@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * */
package atdd.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    @Autowired
    private StationRepository stationRepository;

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody Station station) {
        Station savedStation = stationRepository.save(station);
        String resultUri = String.format("/stations/%d", savedStation.getId());

        return ResponseEntity.created(URI.create(resultUri)).build();
    }

    @GetMapping("/stations")
    public ResponseEntity readStation() {
        List<Station> stations = stationRepository.findAll();

        return new ResponseEntity(stations, HttpStatus.OK);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity readStation(@PathVariable String id) {
        long castingId = Long.parseLong(id);
        Station station = stationRepository.findById(castingId);

        return new ResponseEntity(station, HttpStatus.OK);
    }
}
