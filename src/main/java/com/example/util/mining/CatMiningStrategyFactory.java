package com.example.util.mining;

import java.util.List;

public final class CatMiningStrategyFactory {
    private final List<CatMiningStrategy> strategies = List.of(
        new WhiteCatMiningStrategy(),
        new NoopMiningStrategy()
    );

    public CatMiningStrategy forVariant(String variantId) {
        for (CatMiningStrategy strategy : this.strategies) {
            if (strategy.id().equals(variantId)) {
                return strategy;
            }
        }
        return this.strategies.get(this.strategies.size() - 1);
    }
}
