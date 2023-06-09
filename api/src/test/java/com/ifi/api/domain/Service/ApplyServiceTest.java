package com.ifi.api.domain.Service;

import com.ifi.api.Service.ApplyService;
import com.ifi.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplyServiceTest {


    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("한번만 응모")
    @Test
    public void tryOnce() {
        // given
        applyService.apply(1L);

        // when
        long count = couponRepository.count();

        // then
        assertThat(count).isEqualTo(1);
    }

}