package org.arni.service;

import org.arni.app.BootApp;
import org.arni.jpa.LoginCountRepository;
import org.arni.model.GitUser;
import org.arni.model.LoginCount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BootApp.class })
public class CollectGitUserServiceStressTest {

    @Autowired
    private CollectGitUserService collectGitUserService;

    @Autowired
    private LoginCountRepository loginCountRepository;

    @Test
    public void testStressLoadTransactionWith50Threads() {
        List<Long> responseList = new ArrayList<>();
        List<Callable<Long>> callables = new ArrayList<>();

        //given
        final int ThreadCount = 50;
        GitUser user = GitUser.builder().id(22516408)
                .avatarUrl("url")
                .login("Arni")
                .type("User")
                .createdAt("2016-09-29T07:23:11Z")
                .publicRepos(2)
                .followers(10).build();

        //when
        for (int i = 0; i < ThreadCount; i++) {
            callables.add(() -> collectGitUserService.storeCountedQueryAmount(user));
        }
        ExecutorService pool = Executors.newFixedThreadPool(callables.size());
        try {
            List<Future<Long>> futures = pool.invokeAll(callables);
            for (Future<Long> future : futures) {
                responseList.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }

        //then
        long resultSum = responseList.stream().reduce(0L, Long::sum);
        long expectedSum = LongStream.range(1, ThreadCount + 1).reduce(0L, Long::sum);
        assertEquals(expectedSum, resultSum, "result should be equal to range sum");
    }

    @Test
    public void testWasQueryCounted3Times() {
        //given
        final int NumberOfQueries = 3;
        final long UserId = 123;
        GitUser user = GitUser.builder().id(UserId)
                .avatarUrl("url")
                .login("Nick")
                .type("User")
                .createdAt("2006-09-29T07:23:11Z")
                .publicRepos(4)
                .followers(8).build();

        //when
        collectGitUserService.storeCountedQueryAmount(user);
        collectGitUserService.storeCountedQueryAmount(user);
        collectGitUserService.storeCountedQueryAmount(user);

        //then
        Optional<LoginCount> loginCount = loginCountRepository.findById(UserId);
        assertTrue(loginCount.isPresent(), "Login should be found in database's table");
        LoginCount counted = loginCount.get();
        assertEquals(NumberOfQueries, counted.getRequestCount(), "Counted queries should be equal");
    }
}
