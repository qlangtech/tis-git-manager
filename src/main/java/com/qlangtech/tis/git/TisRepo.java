package com.qlangtech.tis.git;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
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
    public final String repository;
    public final String workBranch;

    private GHBranch currnetBranch;
    private GitHub github;
    private GHRepository repo;
    private TISVersion tagName;
    private String releaseBody;

    private boolean extractIssues = false;

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

    public void initialize(GitHub github, TISVersion tagName, String releaseBody) throws IOException {
        this.repo = github.getRepository(this.repository);
        this.currnetBranch = this.repo.getBranch(this.workBranch);
        Objects.requireNonNull(this.currnetBranch
                , "workBranch:" + workBranch + " relevant branch can not be null");
        this.github = github;
        this.tagName = tagName;
        this.releaseBody = releaseBody;
    }

    public boolean isExtractIssues() {
        return this.extractIssues;
    }

    public TisRepo shallExtractIssues() {
        this.extractIssues = true;
        return this;
    }

    public void createTag(TISVersion newTagName) throws IOException {
        GHTagObject tag = repo.createTag(newTagName.getVersion()
                , "create new Tag:" + newTagName, this.currnetBranch.getSHA1(), "commit");
        GHRef ref = repo.createRef("refs/tags/" + tag.getTag(), tag.getSha());
    }

    enum IssueCategory {
        BUG("bug", "Bug Fix"), Enhance("enhancement", "New Feature"), Other("others", "Others");
        private final String token;
        public final String desc;

        public static IssueCategory parse(GHIssue issue) {
            for (GHLabel label : issue.getLabels()) {
                for (IssueCategory c : IssueCategory.values()) {
                    if (c.token.equalsIgnoreCase(label.getName())) {
                        return c;
                    }
                }
            }
            return IssueCategory.Other;
        }

        IssueCategory(String token, String desc) {
            this.token = token;
            this.desc = desc;
        }

    }

    public static class TISIssue {
        public final URL url;
        public final String title;
        public final int number;

//        public TISIssue(String title, String url) {
//            this.url = url;
//            this.title = title;
//        }

        public TISIssue(GHIssue issue) {
            this.title = issue.getTitle();
            this.url = issue.getHtmlUrl();
            this.number = issue.getNumber();
        }
    }

    public Map<IssueCategory, List<TISIssue>> processIssues() {
        Map<IssueCategory, List<TISIssue>> result = new HashMap<>();
        TISIssue tisIssue = null;
        IssueCategory category = null;
        List<TISIssue> group = null;
        try {
            Set<String> acceptLabels = new HashSet<>();
            acceptLabels.add("3.6.0");
            acceptLabels.add("3.5.0");
            acceptLabels.add("3.6.0-alpha");
            List<GHIssue> issues = repo.getIssues(GHIssueState.ALL);


            for (GHIssue issue : issues) {
                boolean skip = true;
                category = IssueCategory.parse(issue);
                for (GHLabel label : issue.getLabels()) {
                    if (acceptLabels.contains(label.getName())) {
                        skip = false;
                    }
                }
                if (skip) {
                    continue;
                }
                tisIssue = new TISIssue(issue);
                group = result.get(category);
                if (group == null) {
                    group = new ArrayList<>();
                    result.put(category, group);
                }
                group.add(tisIssue);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishRelease() throws IOException {

        GHReleaseBuilder release = repo.createRelease(tagName.getVersion());
        release.name("Release " + tagName);
        release.body(releaseBody);
        release.commitish(this.currnetBranch.getSHA1());
        release.prerelease(true);
        release.draft(false);
        release.create();

    }
}
