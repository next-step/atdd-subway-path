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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class StationController {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationLineRepository stationLineRepository;

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody Station station) {
        Station savedStation = stationRepository.save(station);
        String resultUri = String.format("/stations/%d", savedStation.getId());

        return ResponseEntity.created(URI.create(resultUri)).body(savedStation);
    }

    @GetMapping("/stations")
    public ResponseEntity readStation() {
        List<Station> stations = new ArrayList<Station>();
        stationRepository.findAll().forEach(stations::add);

        return new ResponseEntity(stations, HttpStatus.OK);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity readStation(@PathVariable Long id) {
        Optional<Station> optionalStation = stationRepository.findById(id);

        return getResponseEntityForNullableObject(optionalStation);
    }

    private ResponseEntity getResponseEntityForNullableObject(Optional<Station> optionalStation) {
        if(optionalStation.isPresent()) {
            Station station = optionalStation.get();
            return new ResponseEntity(station, HttpStatus.OK);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/lines")
    public ResponseEntity createStationLine(@RequestBody StationLine stationLine) {
        StationLine savedStationLine = stationLineRepository.save(stationLine);
        String resultUri = String.format("/lines/%d", savedStationLine.getId());

        return ResponseEntity.created(URI.create(resultUri)).body(savedStationLine);
    }

}
