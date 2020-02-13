/*
 *
 * StationLine
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String startTime;
    private String endTime;
    private int stationInterval;

    protected Line() {
    }

    public Line(Long id, String name, String startTime, String endTime, int stationInterval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stationInterval = stationInterval;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getStationInterval() {
        return stationInterval;
    }

    @JsonIgnoreProperties("lines")
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<Station>();
        Station gangnamStation = new Station(1L, "강남역");
        Station yuksamStation = new Station(2L, "역삼역");
        stations.add(gangnamStation);
        stations.add(yuksamStation);
        return stations;
    }
}
