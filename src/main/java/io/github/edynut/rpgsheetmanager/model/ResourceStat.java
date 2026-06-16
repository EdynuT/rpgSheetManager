package io.github.edynut.rpgsheetmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceStat {
    private int atual;
    private int max;

    public ResourceStat() {
        this.atual = 0;
        this.max = 0;
    }

    @JsonCreator
    public ResourceStat(
            @JsonProperty("atual") int atual,
            @JsonProperty("max") int max) {
        this.atual = atual;
        this.max = max;
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // ------------------------------------------------------------------

    public int getAtual() { return atual; }
    public void setAtual(int atual) { this.atual = atual; }

    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }
}
