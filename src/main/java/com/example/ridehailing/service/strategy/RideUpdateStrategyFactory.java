package com.example.ridehailing.service.strategy;

import com.example.ridehailing.model.RideUpdateType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RideUpdateStrategyFactory {

    private final List<RideUpdateStrategy> strategies;

    public RideUpdateStrategyFactory(List<RideUpdateStrategy> strategies) {
        this.strategies = strategies;
    }

    public RideUpdateStrategy getStrategy(RideUpdateType updateType) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(updateType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for update type: " + updateType));
    }
}
