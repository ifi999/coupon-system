package com.ifi.api.Service;

import com.ifi.api.domain.Coupon;
import com.ifi.api.repository.CouponCountRepository;
import com.ifi.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
    }

    /**
     *  lace condition : 두 개이상의 쓰레드에서 공유 자원을 access할 때 발생. single thread에서는 발생하지 않음
     *  -> redis : single thread에서 동작해서 해결 가능. 사용할 incr 명령어의 성능도 준수
     *
     *  redis incr key:value -> key에 대한 value를 1씩 증가시킴
     */
    public void apply(Long userId) {
//        long count = couponRepository.count();
        Long count = couponCountRepository.increment();

        if (count > 100) return;

        couponRepository.save(new Coupon(userId));
    }

}
