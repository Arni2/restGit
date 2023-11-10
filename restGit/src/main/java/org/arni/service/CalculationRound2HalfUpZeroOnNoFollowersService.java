package org.arni.service;

import org.arni.model.GitUser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationRound2HalfUpZeroOnNoFollowersService implements ICalculationService {

    @Override
    public BigDecimal calculateWeight(GitUser user) {
        if (user.getFollowers() != 0) {
            return new BigDecimal(6)
                       .divide(new BigDecimal(user.getFollowers()), 2, RoundingMode.HALF_UP)
                       .multiply(new BigDecimal(2 + user.getPublicRepos()))
                       .stripTrailingZeros();
        } else {
            return BigDecimal.ZERO;
        }
    }
}
