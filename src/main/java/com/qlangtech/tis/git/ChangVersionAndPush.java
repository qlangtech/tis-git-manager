package com.qlangtech.tis.git;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.qlangtech.tis.git.GenerateChangList.newVer;
import static com.qlangtech.tis.git.GenerateChangList.oldVer;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2025-04-14 19:59
 **/
public class ChangVersionAndPush {
    public static void main(String[] args) throws Exception {
        File successLog = new File(".tmp/changVersionAndPush_" + newVer + ".log");
        Set<String> passedRepos = new HashSet<>(FileUtils.readLines(successLog, StandardCharsets.UTF_8));
        try (PrintWriter successWrite = new PrintWriter(FileUtils.openOutputStream(successLog, true))) {

            for (TisRepo repo : Main.tisRelsRepo) {
                if (passedRepos.contains(repo.repository) || repo.localDir == null) {
                    System.out.println("skip repo:" + repo.repository);
                    continue;
                }
                repo.repoSync.updateVersionAndPush(repo);
                successWrite.println(repo.repository);
                System.out.println("success process repo:" + repo.repository);
            }
        }
    }

    public static void commitAndPushRepo(TisRepo repo) throws IOException, GitAPIException {
        // 执行git push
        //try (
        Git git = Git.open(repo.localDir.getDir());
        //) {
        // 2. 添加所有修改文件到暂存区
        Status status = git.status().call();
        // 2. 检查是否有未暂存的变更
        boolean hasUncommittedChanges =
                !status.getUntracked().isEmpty() ||    // 未跟踪文件
                        !status.getModified().isEmpty() ||      // 已修改但未暂存
                        !status.getMissing().isEmpty() ||       // 已删除但未暂存
                        !status.getChanged().isEmpty();         // 其他变更（如文件模式修改）

        if (hasUncommittedChanges) {
            // 3. 执行 add 操作
            git.add().addFilepattern(".").call();
            git.commit()
                    .setMessage("set new version：v" + newVer)
                    .call();
            git.push()
                    // .setCredentialsProvider(CredentialsProviderUtils.getCredentials())
                    .call();
            System.out.println("Commit & Push for：" + repo.repository);
        } else {
            System.out.println("无变更可提交，跳过 add 操作");
            // return; // 直接退出，避免后续 commit/push
        }
    }

    public static void updateAnsibleVersion(TisRepo repo) throws IOException {
        final Pattern PATTERN_VERSION = Pattern.compile("tis_pkg_version:\\s+\"" + StringUtils.replace(oldVer, ".", "\\.") + "\"");
        final String fileVars = "vars.yml";
        File ansibleProject = repo.localDir.getDir();
        final File varsFile = new File(ansibleProject, fileVars);
        File varsFileNew = new File(ansibleProject, fileVars + "." + newVer);
        String line = null;
        boolean findRevision = false;
        Matcher matcher = null;
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(FileUtils.openOutputStream(varsFileNew), StandardCharsets.UTF_8))) {
            LineIterator lineIt = FileUtils.lineIterator(varsFile, StandardCharsets.UTF_8.name());
            while (lineIt.hasNext()) {
                line = lineIt.nextLine();

                if (!findRevision && (matcher = PATTERN_VERSION.matcher(line)).find()) {
                    writer.println(matcher.replaceFirst("tis_pkg_version: " + newVer));
                    findRevision = true;
                } else {
                    writer.println(line);
                }
            }
        }

        if (!findRevision) {
            throw new IllegalStateException("have not find pattern:"
                    + PATTERN_VERSION + " in " + fileVars + " file:" + varsFile.getAbsolutePath());
        }
        File versionBackup = new File(ansibleProject, fileVars + ".versionBackup");
        FileUtils.deleteQuietly(versionBackup);
        FileUtils.moveFile(varsFile, versionBackup);
        FileUtils.moveFile(varsFileNew, varsFile);
    }

    public static void updateVersionForChunjun(TisRepo repo) throws IOException {
        List<VersionElement> versionElementMatchs = new ArrayList<>();
        versionElementMatchs.add(new VersionElement("tis.datax.version"));
        updateVersion(repo, versionElementMatchs);
    }

    /**
     * 更新项目版本基于Revison策略
     *
     * @throws IOException
     */
    public static void updateVersionBaseOnRevisionStrategy(TisRepo repo) throws IOException {

//        final Pattern PATTERN_REVISION = Pattern.compile("<revision>(" + StringUtils.replace(oldVer, ".", "\\.") + ")</revision>");
//        final Pattern PATTERN_VERSION = Pattern.compile("<version>(" + StringUtils.replace(oldVer, ".", "\\.") + ")</version>");
        List<VersionElement> versionElementMatchs = new ArrayList<>();
        versionElementMatchs.add(new VersionElement("revision"));
        versionElementMatchs.add(new VersionElement("version"));
        updateVersion(repo, versionElementMatchs);

//        File pomDir = repo.localDir.getDir();
//        final File pomFile = new File(pomDir, "pom.xml");
//        File pomFileNew = new File(pomDir, "pom.xml." + newVer);
//        String line = null;
//        boolean findRevision = false;
//        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(FileUtils.openOutputStream(pomFileNew), StandardCharsets.UTF_8))) {
//            LineIterator lineIt = FileUtils.lineIterator(pomFile, StandardCharsets.UTF_8.name());
//            while (lineIt.hasNext()) {
//                line = lineIt.nextLine();
//
//                Matcher matcher = null;
//                if (!findRevision && (matcher = PATTERN_REVISION.matcher(line)).find()) {
//                    writer.println(matcher.replaceFirst("<revision>" + newVer + "</revision>"));
//                    findRevision = true;
//                } else if (!findRevision && (matcher = PATTERN_VERSION.matcher(line)).find()) {
//                    writer.println(matcher.replaceFirst("<version>" + newVer + "</version>"));
//                    findRevision = true;
//                } else {
//                    writer.println(line);
//                }
//            }
//        }
//        if (!findRevision) {
//            throw new IllegalStateException("have not find pattern:"
//                    + PATTERN_REVISION + " or " + PATTERN_VERSION + " in pom file:" + pomFile.getAbsolutePath());
//        }
//        File versionBackup = new File(pomDir, "pom.xml.versionBackup");
//        FileUtils.deleteQuietly(versionBackup);
//        FileUtils.moveFile(pomFile, versionBackup);
//        FileUtils.moveFile(pomFileNew, pomFile);
    }

    /**
     * 更新项目版本基于Revison策略
     *
     * @throws IOException
     */
    public static void updateVersion(TisRepo repo, List<VersionElement> versionElementMatchs) throws IOException {

        List<Pair<String, Pattern>> patternMatchs
                = versionElementMatchs.stream().map((ve) -> ve.createPattern(oldVer)).collect(Collectors.toList());

//        final Pattern PATTERN_REVISION = Pattern.compile("<revision>(" + StringUtils.replace(oldVer, ".", "\\.") + ")</revision>");
//        final Pattern PATTERN_VERSION = Pattern.compile("<version>(" + StringUtils.replace(oldVer, ".", "\\.") + ")</version>");
        File pomDir = repo.localDir.getDir();
        final File pomFile = new File(pomDir, "pom.xml");
        File pomFileNew = new File(pomDir, "pom.xml." + newVer);
        String line = null;
        boolean findRevision = false;
        Pattern pattern = null;
        String elementName = null;
        Matcher matcher = null;
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(FileUtils.openOutputStream(pomFileNew), StandardCharsets.UTF_8))) {
            LineIterator lineIt = FileUtils.lineIterator(pomFile, StandardCharsets.UTF_8.name());
            nextline:
            while (lineIt.hasNext()) {
                line = lineIt.nextLine();
                if (findRevision) {
                    writer.println(line);
                    continue;
                }
                for (Pair<String, Pattern> patternMach : patternMatchs) {
                    pattern = patternMach.getValue();
                    elementName = patternMach.getKey();
                    if (!findRevision && (matcher = pattern.matcher(line)).find()) {
                        writer.println(matcher.replaceFirst("<" + elementName + ">" + newVer + "</" + elementName + ">"));
                        findRevision = true;
                        continue nextline;
                    } else {
//                        writer.println(line);
//                        continue nextline;
                    }
                }

                writer.println(line);

//                if (!findRevision && (matcher = PATTERN_REVISION.matcher(line)).find()) {
//                    writer.println(matcher.replaceFirst("<revision>" + newVer + "</revision>"));
//                    findRevision = true;
//                } else if (!findRevision && (matcher = PATTERN_VERSION.matcher(line)).find()) {
//                    writer.println(matcher.replaceFirst("<version>" + newVer + "</version>"));
//                    findRevision = true;
//                } else {
//                    writer.println(line);
//                }
            }
        }
        if (!findRevision) {
            throw new IllegalStateException("have not find pattern:" +
                    patternMatchs.stream().map((match) -> String.valueOf(match.getValue())).collect(Collectors.joining(" or "))
                    + " in pom file:" + pomFile.getAbsolutePath());
        }
        File versionBackup = new File(pomDir, "pom.xml.versionBackup");
        FileUtils.deleteQuietly(versionBackup);
        FileUtils.moveFile(pomFile, versionBackup);
        FileUtils.moveFile(pomFileNew, pomFile);
    }

    private static class VersionElement {
        private final String elementName;
        private Pattern patternElement;

        public VersionElement(String elementName) {
            this.elementName = elementName;
        }

        public Pair<String, Pattern> createPattern(String targetVer) {
            if (this.patternElement == null) {
                String patternElement = StringUtils.replace(elementName, ".", "\\.");
                this.patternElement = Pattern.compile("<" + patternElement + ">(" + StringUtils.replace(targetVer, ".", "\\.") + ")</" + patternElement + ">");
            }
            return Pair.of(elementName, this.patternElement);
        }
    }

}
