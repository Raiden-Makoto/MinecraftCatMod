package com.example.util.revenge;

import java.util.List;

public final class CatRevengeStrategyFactory {
    private final List<CatRevengeStrategy> strategies = List.of(
        new CalicoRevengeStrategy(),
        new NoopRevengeStrategy()
    );

    public CatRevengeStrategy forVariant(String variantId) {
        for (CatRevengeStrategy strategy : this.strategies) {
            if (strategy.id().equals(variantId)) {
                return strategy;
            }
        }
        return this.strategies.get(this.strategies.size() - 1);
    }
}
