package org.arni.service;

import org.arni.jpa.LoginCountRepository;
import org.arni.model.GitUser;
import org.arni.model.LoginCount;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CollectGitUserService {

    private final LoginCountRepository loginCountRepository;

    @Autowired
    public CollectGitUserService(LoginCountRepository loginCountRepository) {
        this.loginCountRepository = loginCountRepository;
    }

    @Retryable(retryFor = {StaleObjectStateException.class, ConstraintViolationException.class, CannotAcquireLockException.class},
            backoff = @Backoff(delay = 50, maxDelay = 300),
            maxAttempts = 100)
    @Transactional(rollbackFor = {StaleObjectStateException.class, ConstraintViolationException.class, CannotAcquireLockException.class},
                   isolation = Isolation.SERIALIZABLE)
    public long storeCountedQueryAmount(GitUser user) {

        //System.out.println(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());

        Optional<LoginCount> loginCountEntity = loginCountRepository.findById(user.getId());

        if (loginCountEntity.isPresent()) {
            loginCountEntity.get().setRequestCount(loginCountEntity.get().getRequestCount() + 1);
            loginCountRepository.save(loginCountEntity.get());
            return loginCountEntity.get().getRequestCount();
        } else {
            LoginCount loginCount = LoginCount.builder().id(user.getId())
                    .login(user.getLogin())
                    .requestCount(1)
                    .build();
            loginCountRepository.save(loginCount);
            return loginCount.getRequestCount();
        }
    }
}
