package com.qlangtech.tis.git;

import java.io.IOException;
import java.util.Objects;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHReleaseBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTagObject;
import org.kohsuke.github.GitHub;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-20 11:51
 **/
public class TisRepo {
    private final String repository;
    private final String workBranch;

    private GHBranch currnetBranch;
    private GitHub github;
    private GHRepository repo;
    private String tagName;
    private String releaseBody;

    public static TisRepo $(String repository) {
        return new TisRepo(repository);
    }

    public static TisRepo $(String repository, String workBranch) {
        return new TisRepo(repository, workBranch);
    }

    private TisRepo(String repository) {
        this(repository, "master");
    }

    private TisRepo(String repository, String workBranch) {
        this.repository = repository;
        this.workBranch = workBranch;
    }

    public void initialize(GitHub github, String tagName, String releaseBody) throws IOException {
        this.repo = github.getRepository(this.repository);
        this.currnetBranch = this.repo.getBranch(this.workBranch);
        Objects.requireNonNull(this.currnetBranch, "workBranch:" + workBranch + " relevant branch can not be null");
        this.github = github;
        this.tagName = tagName;
        this.releaseBody = releaseBody;
    }

    public void createTag(String newTagName) throws IOException {
        GHTagObject tag = repo.createTag(newTagName, "create new Tag:" + newTagName, this.currnetBranch.getSHA1(), "commit");
        GHRef ref = repo.createRef("refs/tags/" + tag.getTag(), tag.getSha());
    }

    public void publishRelease() throws IOException {

        GHReleaseBuilder release = repo.createRelease(tagName);
        release.name("Release " + tagName);
        release.body(releaseBody);
        release.commitish(this.currnetBranch.getSHA1());
        release.prerelease(true);
        release.draft(false);
        release.create();

    }
}
