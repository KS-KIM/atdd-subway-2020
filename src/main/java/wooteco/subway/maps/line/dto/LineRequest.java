package wooteco.subway.maps.line.dto;

import java.time.LocalTime;
import java.util.Objects;

import wooteco.subway.maps.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalTime;
    private Integer fare;

    public LineRequest() {
    }

    public LineRequest(String name, String color, LocalTime startTime, LocalTime endTime, Integer intervalTime,
            Integer fare) {
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.fare = fare;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public Integer getFare() {
        return fare;
    }

    public Line toLine() {
        if (Objects.isNull(fare)) {
            return new Line(name, color, startTime, endTime, intervalTime);
        }
        return new Line(name, color, startTime, endTime, intervalTime, fare);
    }
}
