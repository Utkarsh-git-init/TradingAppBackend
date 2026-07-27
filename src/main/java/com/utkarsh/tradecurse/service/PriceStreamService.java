package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.dto.CurrentPriceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PriceStreamService {
    private final List<SseEmitter> emitters=new CopyOnWriteArrayList<>();
    public SseEmitter subscribe() {
        SseEmitter emitter=new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(()->emitters.remove(emitter));
        emitter.onTimeout(()->emitters.remove(emitter));
        emitter.onError(e->emitters.remove(emitter));
        return emitter;
    }

    public void sendPrices(List<CurrentPriceDto> prices){
        for(SseEmitter emitter:emitters){
            try {
                emitter.send(prices);
            }catch (Exception e){
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}
