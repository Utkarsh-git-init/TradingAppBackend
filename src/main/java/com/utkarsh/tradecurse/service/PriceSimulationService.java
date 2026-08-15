package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.model.MarketState;
import com.utkarsh.tradecurse.model.SimulationState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PriceSimulationService {

    private final Map<Integer, SimulationState> states = new ConcurrentHashMap<>();
    private final MarketState marketState = new MarketState();
    private final CompanyService companyService;

    private static final double FAIR_PRICE_UPDATES_PER_YEAR = 365.0 * 24 * 12;
    private static final double SECONDS_PER_YEAR =
            365.0 * 24 * 60 * 60;

    private static final double DT =
            1.0 / SECONDS_PER_YEAR;

    public PriceSimulationService(CompanyService companyService) {
        marketState.setMarketFactor(0);
        marketState.setSigma(0.0005);
        this.companyService = companyService;
    }

    public SimulationState getState(Company company) {
        return states.computeIfAbsent(
                company.getId(),
                id -> createState(company)
        );
    }

    private SimulationState createState(Company company) {
        SimulationState state = new SimulationState();
        double base = company.getVolatility().getBaseSigma();

        state.setCurrentSigma(base);
        state.setBaseSigma(base);
        state.setMomentum(0);
        state.setBeta(ThreadLocalRandom.current().nextDouble(0.8, 1.2));
        return state;
    }

    public BigDecimal nextPrice(Company company) {
        SimulationState state = getState(company);
        double marketReturn =
                marketState.getMarketFactor()
                        * state.getBeta();
        double deviation = Math.log(
                company.getCurrentPrice().doubleValue() /
                        company.getFairPrice().doubleValue()
        );


        double correction =
                Math.tanh(deviation * 3)
                        * company.getMeanReversion();

        double sigma = state.getCurrentSigma();
        double z = ThreadLocalRandom.current().nextGaussian();


        double mu =
                company.getGrowthRate()
                        + marketReturn
                        - correction;

        double logReturn =
                (mu - sigma * sigma / 2.0) * DT
                        + sigma * Math.sqrt(DT) * z;

        double multiplier = Math.exp(logReturn);
        BigDecimal price = company.getCurrentPrice()
                .multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);

        return price.max(BigDecimal.ONE);
    }


    @Scheduled(cron = "0 * * * * *")
    private void updateSimulationState(){
        updateMarket();
        for (Company company:companyService.getCompanies().values()){
            updateSigma(getState(company));
        }
    }

    private void updateSigma(SimulationState state) {
        double sigma = state.getCurrentSigma();
        // Small random movement
        sigma += ThreadLocalRandom.current().nextGaussian() * 0.002;

        double base = state.getBaseSigma();

        // Prevent it from drifting forever
        sigma += (base - sigma) * 0.02;

        sigma = Math.clamp(sigma, base * 0.5, base * 2.0);

        state.setCurrentSigma(sigma);
    }

    public void updateMarket() {

        double market = marketState.getMarketFactor();

        market += ThreadLocalRandom.current().nextGaussian()* 0.01;

        market += (0 - market) * 0.01;

        market = Math.clamp(market, -0.15, 0.15);

        marketState.setMarketFactor(market);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void updateFairPrices() {

        for (Company company : companyService.getCompanies().values()) {

            double growthPerTick =
                    company.getGrowthRate() / FAIR_PRICE_UPDATES_PER_YEAR;

            double random = ThreadLocalRandom.current()
                    .nextGaussian() * 0.00005;

            BigDecimal multiplier =
                    BigDecimal.valueOf(Math.exp(growthPerTick + random));

            BigDecimal newFairPrice =
                    company.getFairPrice()
                            .multiply(multiplier)
                            .setScale(2, RoundingMode.HALF_UP);

            company.setFairPrice(newFairPrice);
        }
    }
}
