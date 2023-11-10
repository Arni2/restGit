package org.arni.service;

import org.arni.model.GitUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculationRound2HalfUpZeroOnNoFollowersServiceTest {

    @Test
    public void testHappyPathCalculation1() {
        CalculationRound2HalfUpZeroOnNoFollowersService service = new CalculationRound2HalfUpZeroOnNoFollowersService();
        BigDecimal result = service.calculateWeight(GitUser.builder().followers(10).publicRepos(3).build());
        BigDecimal expectedResult = new BigDecimal(3);
        assertEquals(expectedResult, result, "calculation should be equals to " + expectedResult);
    }

    @Test
    public void testHappyPathCalculation2() {
        CalculationRound2HalfUpZeroOnNoFollowersService service = new CalculationRound2HalfUpZeroOnNoFollowersService();
        BigDecimal result = service.calculateWeight(GitUser.builder().followers(7).publicRepos(3).build());
        BigDecimal expectedResult = new BigDecimal("4.3");
        assertEquals(expectedResult, result, "calculation should be equals to " + expectedResult);
    }

    @Test
    public void testBorderSmallCalculationRounded_toZero() {
        CalculationRound2HalfUpZeroOnNoFollowersService service = new CalculationRound2HalfUpZeroOnNoFollowersService();
        BigDecimal result = service.calculateWeight(GitUser.builder().followers(1201).publicRepos(99).build());
        BigDecimal expectedResult = new BigDecimal("0");
        assertEquals(expectedResult, result, "calculation should be ZERO");
    }

    @Test
    public void testSmallCalculationRounded_GreaterThanZero() {
        CalculationRound2HalfUpZeroOnNoFollowersService service = new CalculationRound2HalfUpZeroOnNoFollowersService();
        BigDecimal result = service.calculateWeight(GitUser.builder().followers(1200).publicRepos(99).build());
        BigDecimal expectedResult = new BigDecimal("1.01");
        assertEquals(expectedResult, result, "calculation should be " + expectedResult);
    }

    @Test
    public void testBorderNoFollowersCalculation_shouldBeZero() {
        CalculationRound2HalfUpZeroOnNoFollowersService service = new CalculationRound2HalfUpZeroOnNoFollowersService();
        BigDecimal result = service.calculateWeight(GitUser.builder().followers(0).publicRepos(4).build());
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, result, "calculation should be ZERO on no followers");
    }
}
