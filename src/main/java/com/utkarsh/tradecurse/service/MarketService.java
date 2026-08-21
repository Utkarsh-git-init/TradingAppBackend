package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.dto.MarketDto;
import com.utkarsh.tradecurse.dto.MarketOverviewDto;
import com.utkarsh.tradecurse.dto.MinimalCompanyDto;
import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import com.utkarsh.tradecurse.repository.FifteenMinuteCandleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MarketService {
    private final CompanyService companyService;
    private final FifteenMinuteCandleRepo fifteenMinuteCandleRepo;

    public MarketService(CompanyService companyService, FifteenMinuteCandleRepo fifteenMinuteCandleRepo) {
        this.companyService = companyService;
        this.fifteenMinuteCandleRepo = fifteenMinuteCandleRepo;
    }

    public MarketDto getMarketDto() {


        List<MinimalCompanyDto> companies =new ArrayList<>();
        createMinimalCompanies(companies);

        MarketOverviewDto market =
                createMarketOverview(companies);
        return MarketDto.builder()
                .companies(companies)
                .market(market)
                .build();
    }

    private MarketOverviewDto createMarketOverview(List<MinimalCompanyDto> companies) {
        MarketOverviewDto overview=new MarketOverviewDto();
        overview.setTotalCompanies(companies.size());
        overview.setTotalMarketCap(calculateTotalMarketCap());

        List<BigDecimal> validChanges=companies.stream()
                .map(MinimalCompanyDto::getTwentyFourHourChangePercent)
                .filter(Objects::nonNull)
                .toList();

        overview.setGainers(
                (int) validChanges.stream()
                        .filter(
                                change->change.compareTo(BigDecimal.ZERO)>0
                        )
                        .count()
        );
        overview.setLosers(
                (int) validChanges.stream()
                        .filter(
                                change -> change.compareTo(BigDecimal.ZERO) < 0
                        )
                        .count()
        );
        overview.setUnchanged(
                (int) validChanges.stream()
                        .filter(
                                change -> change.compareTo(BigDecimal.ZERO) == 0
                        )
                        .count()
        );

        if(!validChanges.isEmpty()){
            BigDecimal total=validChanges.stream()
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );
            overview.setAverageChangePercent(
                    total.divide(
                            BigDecimal.valueOf(validChanges.size()),
                            2,
                            RoundingMode.HALF_UP
                    )
            );
        }
        return overview;
    }

    private BigDecimal calculateTotalMarketCap() {
        return companyService.getCompanies().values().stream()
                .filter(company ->
                    company.getCurrentPrice()!=null&&
                            company.getSharesOutstanding()!=null
                )
                .map(
                        company ->
                                company.getCurrentPrice()
                                        .multiply(
                                                BigDecimal.valueOf(company.getSharesOutstanding())
                                        )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public void createMinimalCompanies(List<MinimalCompanyDto> minimalCompanyDtoList) {
        LocalDateTime target= LocalDateTime.now(ZoneId.of("Asia/Kolkata")).minusHours(24);
        for(Company company:companyService.getCompanies().values()){
            MinimalCompanyDto dto =
                    new MinimalCompanyDto();

            dto.setId(company.getId());
            dto.setName(company.getName());
            dto.setSymbol(company.getSymbol());
            dto.setSector(company.getSector());
            dto.setCurrentPrice(company.getCurrentPrice());
            dto.setExchange(company.getExchange());
            dto.setCurrency(company.getCurrency());

            calculate24HourChange(company,dto,target);
            minimalCompanyDtoList.add(dto);
        }
        minimalCompanyDtoList.sort(Comparator.comparing(MinimalCompanyDto::getName));
    }
    private void calculate24HourChange(Company company, MinimalCompanyDto dto, LocalDateTime target) {

        Optional<FifteenMinuteCandle> candle24hrAgo =
                fifteenMinuteCandleRepo
                        .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                                company.getId(),
                                target
                        );
        if (candle24hrAgo.isEmpty()) {return;}
        BigDecimal price24HoursAgo = candle24hrAgo.get().getOpen();
        if (price24HoursAgo.compareTo(BigDecimal.ZERO) == 0) {return;}
        BigDecimal change =
                company.getCurrentPrice()
                        .subtract(price24HoursAgo);
        dto.setTwentyFourHourChange(change);
        dto.setTwentyFourHourChangePercent(
                change.multiply(BigDecimal.valueOf(100))
                        .divide(
                                price24HoursAgo,
                                2,
                                RoundingMode.HALF_UP
                        )
        );
    }
}
