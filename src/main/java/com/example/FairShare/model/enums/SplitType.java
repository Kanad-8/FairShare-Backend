package com.example.FairShare.model.enums;

import lombok.Getter;

@Getter
public enum SplitType {
    EQUAL("Equal"),
    EXACT("Exact"),
    PERCENTAGE("Percentage");

    private final String value;

    SplitType(String value){
        this.value = value;
    }
}
