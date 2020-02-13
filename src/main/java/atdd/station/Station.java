/*
 *
 * Station
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    protected Station() {}

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnoreProperties("stations")
    public List<Line> getLines() {
        List<Line> lines = new ArrayList<Line>();
        Line greenLine = new Line(1L, "2호선", "05:00", "23:00", 10);
        lines.add(greenLine);
        return lines;
    }
}
