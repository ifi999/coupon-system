package com.ifi.api.Service;

import com.ifi.api.domain.Coupon;
import com.ifi.api.producer.CouponCreateProducer;
import com.ifi.api.repository.CouponCountRepository;
import com.ifi.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
    }

    /**
     *  lace condition : 두 개이상의 쓰레드에서 공유 자원을 access할 때 발생. single thread에서는 발생하지 않음
     *  -> redis : single thread에서 동작해서 해결 가능. 사용할 incr 명령어의 성능도 준수
     *
     *  redis incr key:value -> key에 대한 value를 1씩 증가시킴
     *
     *  해결책 1
     *  - redis 활용해서 쿠폰 발급 개수 체크 후 RDBMS에 저장하는 방식
     *  -> 쿠폰 개수가 많아질수록 RDBMS에 부하가 가해짐
     *  --> 쿠폰 전용 DB가 아닌한 다른 비즈니스 로직에 영향을 끼침
     *
     *  해결책 2
     *  - kafka (분산 이벤트 스트리밍 플랫폼) 활용
     *  -> 이벤트 스트리밍이란 소스에서 목적지까지 이벤트를 실시간으로 스트리밍하는 것
     *     Producer --> Topic <-- Consumer 구조
     *
     */
    public void apply(Long userId) {
//        long count = couponRepository.count();
        Long count = couponCountRepository.increment();

        if (count > 100) return;

//        couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId);
    }

}
