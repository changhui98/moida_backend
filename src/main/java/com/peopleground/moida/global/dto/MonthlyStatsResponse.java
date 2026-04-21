package com.peopleground.moida.global.dto;

import java.util.List;

public record MonthlyStatsResponse(String timezone, List<MonthlyStatsPoint> points) {
}
