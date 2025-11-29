package com.qlangtech.tis.git;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

import static com.qlangtech.tis.git.TisRepo.$;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-20 09:29
 **/
public class Main {

    static final TisRepo[] tisRelsRepo = new TisRepo[]{
            $("qlangtech/tis", RepoLocalDir.$("/Users/mozhenghua/j2ee_solution/project/tis-solr"), RepoSync.Revions).shallExtractIssues()
            , $("qlangtech/plugins", RepoLocalDir.$("/Users/mozhenghua/j2ee_solution/project/plugins"), RepoSync.Revions)
            , $("qlangtech/tis-sqlserver-plugin", RepoLocalDir.$("/opt/misc/tis-sqlserver-plugin"), "dev", RepoSync.Revions)
            , $("qlangtech/tis-paimon-plugin", RepoLocalDir.$("/opt/misc/tis-paimon-plugin"), RepoSync.Revions)
            , $("qlangtech/tis-plugins-commercial", RepoLocalDir.$("/opt/misc/tis-plugins-commercial"), RepoSync.Revions)
            , $("qlangtech/ng-tis", RepoLocalDir.$("/Users/mozhenghua/j2ee_solution/project/tis-console"), RepoSync.JustCommitPush)
            , $("qlangtech/tis-archetype-plugin", RepoLocalDir.$("/opt/misc/tis-archetype-plugin"), RepoSync.Revions)
            , $("qlangtech/tis-ansible", RepoLocalDir.$("/opt/misc/tis-ansible"), RepoSync.AnsiblePOM)
            , $("qlangtech/tis-git-manager", RepoLocalDir.$("/opt/misc/tis-git-manager"), RepoSync.JustCommitPush)
            , $("qlangtech/update-center2", RepoLocalDir.$("/opt/misc/update-center2"), RepoSync.Revions)
            , $("qlangtech/DataX", RepoLocalDir.$("/opt/misc/DataX"), RepoSync.Revions)
            // , $("qlangtech/tis-git-manager")
            , $("qlangtech/debezium"//, RepoLocalDir.$("/opt/misc/debezium")
            , "tis.v1.9.8.Final", RepoSync.JustCommitPush)
            , $("qlangtech/flink-cdc" //, RepoLocalDir.$("/opt/misc/flink-cdc")
            , "tis-3.4.0", RepoSync.JustCommitPush)
            , $("qlangtech/flink"//, RepoLocalDir.$("/opt/misc/flink-1.18.1/flink")
            , "tis-1.20.1", RepoSync.JustCommitPush)
            , $("qlangtech/chunjun", RepoLocalDir.$("/opt/misc/chunjun"), "tis-v1.12.5", RepoSync.ChunjunPOM)
            , $("qlangtech/dolphinscheduler", "tis-3.2.2", RepoSync.JustCommitPush)
            , $("qlangtech/tis-doc"/*, RepoLocalDir.$("/opt/misc/tis-docs2/docs/plugin")*/, RepoSync.JustCommitPush)
            , $("qlangtech/hudi", "tis-release-0.10.1", RepoSync.JustCommitPush)
            //, $("qlangtech/zeppelin", "tis-v0.10.1")
    };


//    static final TisRepo[] tisRelsRepo = new TisRepo[]{
//            $("qlangtech/tis-ansible")
//    };

    /**
     * https://github-api.kohsuke.org/index.html
     */
    public static void main(String[] args) throws Exception {
        // GitHub github = GitHub.connect();
        // Once your ~/.github property file is properly configured, you can obtain a GitHub instance using
        GenerateChangList changList = new GenerateChangList();

        // 首先需要校验证本地仓库中是否还存在没有deploy的内容
        for (TisRepo tisRepo : tisRelsRepo) {

        }

        // final String tagName = "v3.6.0-alpha";
        System.out.println("Start Release Version:" + GenerateChangList.tagName);
        TISVersion tagName = GenerateChangList.tagName;
        final String releaseBody = FileUtils.readFileToString(new File("release/" + tagName + ".md"), Charset.forName("utf8"));
        // GHRepository repo = github.getRepository("baisui1981/tisearch");
        System.out.println("1. Initialize");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.initialize(changList.getGithub(), tagName, releaseBody);
            System.out.println("## init module:" + tisRepo.repository);
        }

        System.out.println("2. Create Tag");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.createTag(tagName);
            System.out.println("## create tag for module:" + tisRepo.repository);
        }

        System.out.println("3. Publish Release");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.publishRelease();
            System.out.println("## push release for module:" + tisRepo.repository);
        }
        System.out.println("Successful Release Version:" + GenerateChangList.tagName);
//        GHRepository repo = github.createRepository(
//                "new-repository", "this is my new repository",
//                "https://github.com/ben2077/tisearch", true/*public*/);
        //repo.addCollaborators(github.getUser("abayer"),github.getUser("rtyler"));

//        PagedIterable<GHTag> tags = repo.listTags();
//        for (GHTag tag : tags) {
//            System.out.println(ToStringBuilder.reflectionToString(tag));
//        }
//
//        for (Map.Entry<String, GHBranch> e : repo.getBranches().entrySet()) {
//            System.out.println(e.getKey() + "->" + e.getValue());
//        }

//        String masterObject = repo.getBranch("master").getSHA1();
//
//
//        final String object = "05ab13a84d763bf497a0afa054d592b23ca78fb6";
//
//
//        GHTagObject tag = repo.createTag(tagName, "create new Tag:" + tagName, object, "commit");
//
//        GHRef ref = repo.createRef("refs/tags/" + tag.getTag(), tag.getSha());
//
////        PagedIterable<GHTag> tags = repo.listTags();
////        for (GHTag tag : tags) {
////            System.out.println(ToStringBuilder.reflectionToString(tag));
////        }
//        GHReleaseBuilder release = repo.createRelease(tagName);
//        release.name("Release " + tagName);
//        release.body(releaseBody);
//        release.commitish(object);
//        release.prerelease(true);
//        release.draft(false);
//        release.create();

        //System.out.println(ToStringBuilder.reflectionToString(tag));

    }
}
