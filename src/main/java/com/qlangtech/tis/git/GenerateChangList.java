package com.qlangtech.tis.git;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import com.qlangtech.tis.git.TisRepo.IssueCategory;
import com.qlangtech.tis.git.TisRepo.TISIssue;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-20 13:45
 **/
public class GenerateChangList {
    public static final TISVersion tagName = new TISVersion("v3.8.0");

    private final GitHub github;


    public GenerateChangList() {
        try {
            this.github = GitHubBuilder.fromPropertyFile().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GitHub getGithub() {
        return github;
    }

    public String getReleaseBody() {
        return tagName.getReleaseBody();
    }

    public void generate() throws IOException {

        // GenerateChangList changList = new GenerateChangList();
        Map<IssueCategory, List<TISIssue>> groupIssue = new HashMap<>();
        for (TisRepo repo : Main.tisRelsRepo) {
            if (!repo.isExtractIssues()) {
                continue;
            }
            repo.initialize(this.getGithub(), GenerateChangList.tagName, null);
        }
        for (TisRepo repo : Main.tisRelsRepo) {
            if (repo.isExtractIssues()) {
                repo.processIssues().forEach((key, group) -> {
                    List<TISIssue> g = groupIssue.get(key);
                    if (g == null) {
                        groupIssue.put(key, group);
                    } else {
                        g.addAll(group);
                    }
                });
            }
        }


        StringBuffer content = new StringBuffer();
        for (Map.Entry<IssueCategory, List<TISIssue>> entry : groupIssue.entrySet()) {
            content.append("\n## ").append(entry.getKey().desc).append("\n\n");
            for (TISIssue issue : entry.getValue()) {
                content.append("* ").append(issue.title).append("[#").append(issue.number).append("](").append(issue.url).append(")\n");
            }
        }

        content.append("\n## ").append("Installation").append("\n\n");

        content.append("* [").append("TIS Package").append("](")
                .append("http://tis-release.oss-cn-beijing.aliyuncs.com/").append(tagName.versionNum).append("/tis/tis-uber.tar.gz").append(") ");
        content.append("[安装说明](https://tis.pub/docs/install/tis/uber/)\n");

        content.append("* [").append("TIS Flink Standalone Package").append("](")
                .append("http://tis-release.oss-cn-beijing.aliyuncs.com/").append(tagName.versionNum).append("/tis/flink-tis-1.13.1-bin.tar.gz").append(") ");
        content.append("[安装说明](https://tis.pub/docs/install/flink-cluster/standalone/)\n");

        content.append("* ").append("TIS Zeppeline NoteBook Installation");
        content.append("[安装说明](https://tis.pub/docs/install/zeppelin)\n");


        content.append("\n## ").append("Related Projects").append("\n\n");

        for (TisRepo repo : Main.tisRelsRepo) {
            content.append("* [").append(repo.repository).append("](").append("https://github.com/").append(repo.repository).append("/tree/").append(repo.workBranch).append(")\n");
        }

        tagName.writeReleaseNote(content);

        //  FileUtils.writeStringToFile(tagName.getReleaseNoteFile(), content.toString(), utf8, false);
    }


    public static void main(String[] args) throws Exception {
        GenerateChangList changeList = new GenerateChangList();

        changeList.generate();
        // System.out.println(groupIssue);
        // final String releaseBody = FileUtils.readFileToString(releaseNoteFile, Charset.forName("utf8"));
    }


}
