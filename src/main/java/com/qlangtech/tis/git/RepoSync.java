package com.qlangtech.tis.git;

import java.io.IOException;
import java.util.function.Consumer;

import static com.qlangtech.tis.git.ChangVersionAndPush.commitAndPushRepo;
import static com.qlangtech.tis.git.ChangVersionAndPush.updateAnsibleVersion;
import static com.qlangtech.tis.git.ChangVersionAndPush.updateVersionBaseOnRevisionStrategy;
import static com.qlangtech.tis.git.ChangVersionAndPush.updateVersionForChunjun;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2025-04-15 09:13
 **/
public enum RepoSync {
    Revions((repo) -> {
        try {
            updateVersionBaseOnRevisionStrategy(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, (repo) -> {
        try {
            commitAndPushRepo(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    /**
     * 只提交变更
     */
    JustCommitPush((repo) -> {
    }, (repo) -> {
        try {
            commitAndPushRepo(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    /**
     * 支持ansible版本更新
     */
    AnsiblePOM((repo) -> {
        try {
            updateAnsibleVersion(repo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }, (repo) -> {
        try {
            commitAndPushRepo(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    ChunjunPOM((repo) -> {
        try {
            updateVersionForChunjun(repo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }, (repo) -> {
        try {
            commitAndPushRepo(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });


    private final Consumer<TisRepo> localVerUpdate;
    private final Consumer<TisRepo> remoteRepoSyn;

    private RepoSync(Consumer<TisRepo> localVerUpdate, Consumer<TisRepo> remoteRepoSyn) {
        this.localVerUpdate = localVerUpdate;
        this.remoteRepoSyn = remoteRepoSyn;
    }

    public void updateVersionAndPush(TisRepo repo) {
        localVerUpdate.accept(repo);
        remoteRepoSyn.accept(repo);
    }
}
