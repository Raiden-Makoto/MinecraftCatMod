package com.example.util.buffs;

import java.util.List;

public final class CatBuffStrategyFactory {
    private final List<CatBuffStrategy> strategies = List.of(
        new AllBlackCatBuffStrategy(),
        new OrangeCatBuffStrategy(),
        new JellieCatBuffStrategy(),
        new NoopCatBuffStrategy()
    );

    public CatBuffStrategy forVariant(String variantId) {
        for (CatBuffStrategy strategy : this.strategies) {
            if (strategy.id().equals(variantId)) {
                return strategy;
            }
        }
        return this.strategies.get(this.strategies.size() - 1);
    }
}
