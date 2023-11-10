package org.arni.service;

import org.arni.model.GitUser;

import java.math.BigDecimal;

public interface ICalculationService {
    BigDecimal calculateWeight(GitUser user);
}
