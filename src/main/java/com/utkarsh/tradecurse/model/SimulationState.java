package com.utkarsh.tradecurse.model;

import lombok.Data;

@Data
public class SimulationState {
    private double currentSigma;
    private double baseSigma;
    private double momentum;
    private double beta;
}
