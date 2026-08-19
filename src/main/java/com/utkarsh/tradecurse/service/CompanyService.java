package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.dto.CompanyDto;
import com.utkarsh.tradecurse.dto.CompanyFundamentals;
import com.utkarsh.tradecurse.dto.PriceRangeDto;
import com.utkarsh.tradecurse.entity.*;
import com.utkarsh.tradecurse.enums.Interval;
import com.utkarsh.tradecurse.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;
    private final MinuteCandleRepo minuteCandleRepo;
    private final FiveMinuteCandleRepo fiveMinuteCandleRepo;
    private final FifteenMinuteCandleRepo fifteenMinuteCandleRepo;
    private final HourCandleRepo hourCandleRepo;
    private final SixHourCandleRepo sixHourCandleRepo;
    private final DayCandleRepo dayCandleRepo;
    @Getter
    private Map<Integer, Company> companies=new ConcurrentHashMap<>();

    public CompanyService(CompanyRepo companyRepo, MinuteCandleRepo minuteCandleRepo, FiveMinuteCandleRepo fiveMinuteCandleRepo, FifteenMinuteCandleRepo fifteenMinuteCandleRepo, HourCandleRepo hourCandleRepo, SixHourCandleRepo sixHourCandleRepo, DayCandleRepo dayCandleRepo) {
        this.companyRepo = companyRepo;
        this.minuteCandleRepo = minuteCandleRepo;
        this.fiveMinuteCandleRepo = fiveMinuteCandleRepo;
        this.fifteenMinuteCandleRepo = fifteenMinuteCandleRepo;
        this.hourCandleRepo = hourCandleRepo;
        this.sixHourCandleRepo = sixHourCandleRepo;
        this.dayCandleRepo = dayCandleRepo;
    }

    @PostConstruct
    public void loadCompanies(){
        companyRepo.findAll()
                .forEach(company -> {
                    companies.put(company.getId(),company);
                });
    }
    public Company addCompany(Company company) {
        return companyRepo.save(company);
    }

    public CompanyDto getCompanyById(Integer companyId) {
        CompanyDto companyDto=new CompanyDto();
        Company company=companies.get(companyId);
        companyDto.setId(companyId);
        companyDto.setName(company.getName());
        companyDto.setSector(company.getSector());
        companyDto.setDescription(company.getDescription());
        companyDto.setCurrency(company.getCurrency());
        companyDto.setExchange(company.getExchange());
        companyDto.setFoundedYear(company.getFoundedYear());
        companyDto.setCurrentPrice(company.getCurrentPrice());
        companyDto.setEmployees(company.getEmployees());
        companyDto.setSymbol(company.getSymbol());

        CompanyFundamentals fundamentals=new CompanyFundamentals();
        fundamentals.setEps(company.getEps());
        fundamentals.setDividendYield(company.getDividendYield());
        fundamentals.setPeRatio(company.getPeRatio());
        fundamentals.setSharesOutstanding(company.getSharesOutstanding());
        companyDto.setFundamentals(fundamentals);

        companyDto.setRanges(new HashMap<>());
        setPriceRanges(companyDto.getRanges(),company);

        companyDto.setChanges(new HashMap<>());
        setPricePercentChange(companyDto.getChanges(),company);
        return companyDto;
    }

    private void setPricePercentChange(Map<Interval, BigDecimal> changes, Company company) {
        LocalDateTime now=LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        for(Interval interval:Interval.values()){
            changes.put(
                    interval,
                    setChange(
                            company,
                            interval,
                            now.minus(interval.getDuration())
                    )
            );
        }
    }

    private BigDecimal setChange(Company company, Interval interval, LocalDateTime from) {
        Integer companyId=company.getId();
        BigDecimal oldPrice= switch (interval){
            case ONE_HOUR -> minuteCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId, from)
                    .map(MinuteCandle::getOpen)
                    .orElse(null);
            case SIX_HOUR -> fiveMinuteCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId,from
                    )
                    .map(FiveMinuteCandle::getOpen)
                    .orElse(null);
            case TWENTY_FOUR_HOURS -> fifteenMinuteCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId,from
                    )
                    .map(FifteenMinuteCandle::getOpen)
                    .orElse(null);
            case ONE_WEEK -> hourCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId,from)
                    .map(HourCandle::getOpen)
                    .orElse(null);
            case ONE_MONTH -> sixHourCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId,from)
                    .map(SixHourCandle::getOpen)
                    .orElse(null);
            case ONE_YEAR -> dayCandleRepo
                    .findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                            companyId,from)
                    .map(DayCandle::getOpen)
                    .orElse(null);
        };

        if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return company.getCurrentPrice()
                .subtract(oldPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(oldPrice, 2, RoundingMode.HALF_UP);
    }

    private void setPriceRanges(Map<Interval, PriceRangeDto> ranges, Company company) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        for(Interval interval:Interval.values()){
            ranges.put(
                    interval,
                    getRange(
                            company.getId(),
                            interval,
                            now.minus(interval.getDuration())
                    )
            );
        }
    }

    private PriceRangeDto getRange(Integer companyId, Interval interval,LocalDateTime from) {
        return switch (interval) {
            case ONE_HOUR -> minuteCandleRepo.findRange(companyId,from);
            case SIX_HOUR -> fiveMinuteCandleRepo.findRange(companyId,from);
            case TWENTY_FOUR_HOURS -> fifteenMinuteCandleRepo.findRange(companyId,from);
            case ONE_WEEK -> hourCandleRepo.findRange(companyId,from);
            case ONE_MONTH -> sixHourCandleRepo.findRange(companyId,from);
            case ONE_YEAR -> dayCandleRepo.findRange(companyId,from);
        };
    }

    public ResponseEntity<?> updateCompany(Integer companyId, CompanyDto dto) {

        Company company = companyRepo.findById(companyId)
                .orElse(null);

        if (company == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company not found");

        if(dto.getSymbol() !=null)
            company.setSymbol(dto.getSymbol());

        if (dto.getDescription() != null)
            company.setDescription(dto.getDescription());

        if (dto.getEmployees() != null)
            company.setEmployees(dto.getEmployees());

        if (dto.getFoundedYear() != null)
            company.setFoundedYear(dto.getFoundedYear());

        if (dto.getExchange() != null)
            company.setExchange(dto.getExchange());

        if (dto.getCurrency() != null)
            company.setCurrency(dto.getCurrency());

        if (dto.getFundamentals() != null) {
            CompanyFundamentals fundamentals=dto.getFundamentals();
            if (fundamentals.getPeRatio() != null)
                company.setPeRatio(fundamentals.getPeRatio());

            if (fundamentals.getEps() != null)
                company.setEps(fundamentals.getEps());

            if (fundamentals.getDividendYield() != null)
                company.setDividendYield(fundamentals.getDividendYield());

            if(fundamentals.getSharesOutstanding()!=null)
                company.setSharesOutstanding(fundamentals.getSharesOutstanding());
        }

        Company updatedCompany=companyRepo.save(company);
        companies.put(companyId,updatedCompany);
        return ResponseEntity.ok(updatedCompany);
    }
}
