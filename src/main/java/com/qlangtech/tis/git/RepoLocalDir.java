package com.qlangtech.tis.git;

import java.io.File;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2025-02-10 10:19
 **/
public class RepoLocalDir {
    private final File localDir;

    public static RepoLocalDir $(String dirPath) {
        return new RepoLocalDir(dirPath);
    }

    private RepoLocalDir(String localDir) {
        this.localDir = new File(localDir);
        if (!this.localDir.exists()) {
            throw new IllegalStateException("localDir:" + this.localDir.getAbsolutePath() + " must be exist");
        }
    }
}
