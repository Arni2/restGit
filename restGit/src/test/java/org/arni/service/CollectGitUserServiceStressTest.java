package org.arni.service;

import org.arni.app.BootApp;
import org.arni.model.GitUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.esapi.errors.EncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BootApp.class })
public class CollectGitUserServiceStressTest {

    @Autowired
    private CollectGitUserService collectGitUserService;

    @Test
    public void testStressTransactionWith50Threads() throws EncodingException, IOException {
        final int ThreadCount = 50;
        List<Long> responseList = new ArrayList<>();
        List<Callable<Long>> callables = new ArrayList<>();
        GitUser user = GitUser.builder().id(22516408)
                .avatarUrl("url")
                .login("Arni")
                .type("User")
                .createdAt("2016-09-29T07:23:11Z")
                .publicRepos(2)
                .followers(10).build();
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
            long resultSum = responseList.stream().reduce(0L, Long::sum);
            long expectedSum = LongStream.range(1, ThreadCount + 1).reduce(0L, Long::sum);
            assertEquals(expectedSum, resultSum, "result should be equal to range sum");
        }
    }
}
