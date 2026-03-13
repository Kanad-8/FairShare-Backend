package com.example.FairShare.service.strategy;

import com.example.FairShare.model.enums.SplitType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Component
public class SplitStrategyFactory {

    private final Map<SplitType,SplitStrategy> strategies;

    public SplitStrategyFactory(Set<SplitStrategy> strategySet){
        this.strategies = new EnumMap<>(SplitType.class);

        for(SplitStrategy strategy:strategySet){
            this.strategies.put(strategy.getSplitType(),strategy);
        }
    }

    public SplitStrategy getStrategy(SplitType type){
        SplitStrategy strategy = strategies.get(type);
        if(strategy == null){
            throw new IllegalArgumentException("Unsupported Split Type: "+ type);
        }
        return strategy;
    }
}
