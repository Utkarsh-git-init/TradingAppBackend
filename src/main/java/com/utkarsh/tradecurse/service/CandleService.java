package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.model.*;
import com.utkarsh.tradecurse.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class CandleService {
    private final AtomicReference<ConcurrentHashMap<Integer, CurrentCandle>> candles = new AtomicReference<>(new ConcurrentHashMap<>());

    private final CompanyService companyService;
    private final CompanyRepo companyRepo;
    private final MinuteCandleRepo minuteCandleRepo;
    private final FiveMinuteCandleRepo fiveMinuteCandleRepo;
    private final FifteenMinuteCandleRepo fifteenMinuteCandleRepo;
    private final HourCandleRepo hourCandleRepo;
    private final SixHourCandleRepo sixHourCandleRepo;
    private final DayCandleRepo dayCandleRepo;

    public CandleService(CompanyService companyService, CompanyRepo companyRepo, MinuteCandleRepo minuteCandleRepo, FiveMinuteCandleRepo fiveMinuteCandleRepo, FifteenMinuteCandleRepo fifteenMinuteCandleRepo, HourCandleRepo hourCandleRepo, SixHourCandleRepo sixHourCandleRepo, DayCandleRepo dayCandleRepo) {
        this.companyService = companyService;
        this.companyRepo = companyRepo;
        this.minuteCandleRepo = minuteCandleRepo;
        this.fiveMinuteCandleRepo = fiveMinuteCandleRepo;
        this.fifteenMinuteCandleRepo = fifteenMinuteCandleRepo;
        this.hourCandleRepo = hourCandleRepo;
        this.sixHourCandleRepo = sixHourCandleRepo;
        this.dayCandleRepo = dayCandleRepo;
    }

    public void updatePrice(Integer companyId, BigDecimal newPrice){
        candles.get().compute(companyId, (id,candle)->{
            if(candle==null){
                CurrentCandle c=new CurrentCandle();
                c.setOpen(newPrice);
                c.setHigh(newPrice);
                c.setLow(newPrice);
                c.setClose(newPrice);
                return c;
            }
            candle.setLow(newPrice.min(candle.getLow()));
            candle.setHigh(newPrice.max(candle.getHigh()));
            candle.setClose(newPrice);
            return candle;
        });

    }

    @Scheduled(cron = "0 * * * * *")
    public void persist(){
        log.info("saving candle and price to db");

        ConcurrentHashMap<Integer, CurrentCandle> snapshot =
                candles.getAndSet(new ConcurrentHashMap<>());

        List<MinuteCandle> minuteCandleList =new ArrayList<>();
        LocalDateTime timestamp=LocalDateTime.now(ZoneId.of("Asia/Kolkata")).truncatedTo(ChronoUnit.MINUTES);
        for(Company company:companyService.getCompanies().values()){
            MinuteCandle minuteCandle=new MinuteCandle();
            CurrentCandle candle=snapshot.get(company.getId());
            if(candle==null)
                continue;
            minuteCandle.setCompany(company);
            minuteCandle.setOpen(candle.getOpen());
            minuteCandle.setClose(candle.getClose());
            minuteCandle.setHigh(candle.getHigh());
            minuteCandle.setLow(candle.getLow());
            minuteCandle.setTimestamp(timestamp.minusMinutes(1));
            company.setCurrentPrice(candle.getClose());
            minuteCandleList.add(minuteCandle);
        }
        companyRepo.saveAll(companyService.getCompanies().values());
        minuteCandleRepo.saveAll(minuteCandleList);
        if(timestamp.getMinute()%5==0)
            createFiveMinuteCandles(timestamp);
        if(timestamp.getMinute()%15==0)
            createFifteenMinuteCandles(timestamp);
        if(timestamp.getMinute()==0)
            createHourCandle(timestamp);
        if(timestamp.getHour()%6==0&&timestamp.getMinute()==0)
            createSixHourCandle(timestamp);
        if(timestamp.getHour()==0&&timestamp.getMinute()==0)
            createDayCandle(timestamp);
    }

    private void createDayCandle(LocalDateTime timestamp) {
        List<DayCandle> dayCandleList=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            List<SixHourCandle> candleList=sixHourCandleRepo
                    .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                            company,timestamp.minusDays(1),timestamp);

            if(candleList.isEmpty()){
                continue;
            }
            if (candleList.size() < 4) {
                log.warn("Creating partial Day candle for {} ({} 6hr candles)",
                        company.getId(), candleList.size());
            }

            BigDecimal open=candleList.getFirst().getOpen();
            BigDecimal close=candleList.getLast().getClose();
            BigDecimal high=candleList.getFirst().getHigh();
            BigDecimal low=candleList.getFirst().getLow();
            for(SixHourCandle candle:candleList){
                low=low.min(candle.getLow());
                high=high.max(candle.getHigh());
            }
            DayCandle candle=new DayCandle();
            candle.setCompany(company);
            candle.setTimestamp(timestamp.minusDays(1));
            candle.setOpen(open);
            candle.setClose(close);
            candle.setLow(low);
            candle.setHigh(high);
            dayCandleList.add(candle);
        }
        dayCandleRepo.saveAll(dayCandleList);
    }

    private void createSixHourCandle(LocalDateTime timestamp) {
        List<SixHourCandle> sixHourCandleList=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            List<HourCandle> candleList=hourCandleRepo
                    .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                            company,timestamp.minusHours(6),timestamp);

            if(candleList.isEmpty()){
                continue;
            }
            if (candleList.size() < 6) {
                log.warn("Creating partial 6-hour candle for {} ({} hour candles)",
                        company.getId(), candleList.size());
            }

            BigDecimal open=candleList.getFirst().getOpen();
            BigDecimal close=candleList.getLast().getClose();
            BigDecimal high=candleList.getFirst().getHigh();
            BigDecimal low=candleList.getFirst().getLow();
            for(HourCandle candle:candleList){
                low=low.min(candle.getLow());
                high=high.max(candle.getHigh());
            }
            SixHourCandle candle=new SixHourCandle();
            candle.setCompany(company);
            candle.setTimestamp(timestamp.minusHours(6));
            candle.setOpen(open);
            candle.setClose(close);
            candle.setLow(low);
            candle.setHigh(high);
            sixHourCandleList.add(candle);
        }
        sixHourCandleRepo.saveAll(sixHourCandleList);
    }

    private void createHourCandle(LocalDateTime timestamp) {
        List<HourCandle> hourCandleList=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            List<FifteenMinuteCandle> candleList=fifteenMinuteCandleRepo
                    .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                            company,timestamp.minusHours(1),timestamp);

            if(candleList.isEmpty()){
                continue;
            }
            if (candleList.size() < 4) {
                log.warn("Creating partial 1-hour candle for {} ({} 15min candles)",
                        company.getId(), candleList.size());
            }

            BigDecimal open=candleList.getFirst().getOpen();
            BigDecimal close=candleList.getLast().getClose();
            BigDecimal high=candleList.getFirst().getHigh();
            BigDecimal low=candleList.getFirst().getLow();
            for(FifteenMinuteCandle candle:candleList){
                low=low.min(candle.getLow());
                high=high.max(candle.getHigh());
            }
            HourCandle candle=new HourCandle();
            candle.setCompany(company);
            candle.setTimestamp(timestamp.minusHours(1));
            candle.setOpen(open);
            candle.setClose(close);
            candle.setLow(low);
            candle.setHigh(high);
            hourCandleList.add(candle);
        }
        hourCandleRepo.saveAll(hourCandleList);
    }

    private void createFifteenMinuteCandles(LocalDateTime timestamp) {
        List<FifteenMinuteCandle> fifteenMinuteCandleList =new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            List<FiveMinuteCandle> candleList=fiveMinuteCandleRepo
                    .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                            company,timestamp.minusMinutes(15),timestamp);

            if(candleList.isEmpty()){
                continue;
            }
            if (candleList.size() < 3) {
                log.warn("Creating partial 15-minute candle for {} ({} 5min candles)",
                        company.getId(), candleList.size());
            }

            BigDecimal open=candleList.getFirst().getOpen();
            BigDecimal close=candleList.getLast().getClose();
            BigDecimal high=candleList.getFirst().getHigh();
            BigDecimal low=candleList.getFirst().getLow();
            for(FiveMinuteCandle candle:candleList){
                low=low.min(candle.getLow());
                high=high.max(candle.getHigh());
            }
            FifteenMinuteCandle candle=new FifteenMinuteCandle();
            candle.setCompany(company);
            candle.setTimestamp(timestamp.minusMinutes(15));
            candle.setOpen(open);
            candle.setClose(close);
            candle.setLow(low);
            candle.setHigh(high);
            fifteenMinuteCandleList.add(candle);
        }
        fifteenMinuteCandleRepo.saveAll(fifteenMinuteCandleList);
    }

    private void createFiveMinuteCandles(LocalDateTime timestamp) {
        List<FiveMinuteCandle> fiveMinuteCandlesList=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            List<MinuteCandle> candleList=minuteCandleRepo
                    .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                            company,timestamp.minusMinutes(5),timestamp);

            if(candleList.isEmpty()){
                continue;
            }
            if (candleList.size() < 5) {
                log.warn("Creating partial 5-minute candle for {} ({} minute candles)",
                        company.getId(), candleList.size());
            }

            BigDecimal open=candleList.getFirst().getOpen();
            BigDecimal close=candleList.getLast().getClose();
            BigDecimal high=candleList.getFirst().getHigh();
            BigDecimal low=candleList.getFirst().getLow();
            for(MinuteCandle candle:candleList){
                low=low.min(candle.getLow());
                high=high.max(candle.getHigh());
            }
            FiveMinuteCandle candle =new FiveMinuteCandle();
            candle.setCompany(company);
            candle.setTimestamp(timestamp.minusMinutes(5));
            candle.setOpen(open);
            candle.setClose(close);
            candle.setLow(low);
            candle.setHigh(high);
            fiveMinuteCandlesList.add(candle);
        }
        fiveMinuteCandleRepo.saveAll(fiveMinuteCandlesList);
    }
}
