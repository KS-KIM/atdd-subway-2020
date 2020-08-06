package wooteco.subway.maps.map.domain;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class SubwayPath {
    private static final int DEFAULT_FARE = 1_250;
    private static final int ADDITIONAL_DISTANCE_OVER_FARE_UPPER_BOUND = 40;
    private static final double DISTANCE_OVER_FARE_UNIT = 5.0;
    private static final int OVER_FARE_UPPER_BOUND = 10;
    private static final int DISTANCE_OVER_FARE_PER_UNIT = 100;

    private List<LineStationEdge> lineStationEdges;

    public SubwayPath(List<LineStationEdge> lineStationEdges) {
        this.lineStationEdges = lineStationEdges;
    }

    public List<Long> extractStationId() {
        List<Long> stationIds = Lists.newArrayList(lineStationEdges.get(0).getLineStation().getPreStationId());
        stationIds.addAll(lineStationEdges.stream()
                .map(it -> it.getLineStation().getStationId())
                .collect(Collectors.toList()));

        return stationIds;
    }

    public List<Long> extractLineId() {
        return lineStationEdges.stream()
                .map(LineStationEdge::getLineId)
                .collect(Collectors.toList());
    }

    public int calculateDuration() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDuration()).sum();
    }

    public int calculateDistance() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDistance()).sum();
    }

    public int calculateFare(int distance) {
        int totalFare = DEFAULT_FARE;
        if (distance > OVER_FARE_UPPER_BOUND) {
            totalFare += calculateOverFareByDistance(distance - OVER_FARE_UPPER_BOUND);
        }
        return totalFare;
    }

    private int calculateOverFareByDistance(int distance) {
        if (distance == 0) {
            return 0;
        }
        if (distance > ADDITIONAL_DISTANCE_OVER_FARE_UPPER_BOUND) {
            distance = ADDITIONAL_DISTANCE_OVER_FARE_UPPER_BOUND;
        }
        return (int)(Math.ceil(distance / DISTANCE_OVER_FARE_UNIT) * DISTANCE_OVER_FARE_PER_UNIT);
    }

    private int discountByAge(int fare, int age) {
        if (age >= 13 && age < 19) {
            return fare - ((fare - 350) / 5);
        }
        if (age >= 6 && age < 13) {
            return fare - ((fare - 350) / 2);
        }
        return fare;
    }

    public List<LineStationEdge> getLineStationEdges() {
        return lineStationEdges;
    }
}
