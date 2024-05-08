package com.qlangtech.tis.git;

import static com.qlangtech.tis.git.TisRepo.$;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-20 09:29
 **/
public class Main {

    static final TisRepo[] tisRelsRepo = new TisRepo[]{
            $("qlangtech/tis").shallExtractIssues()
            , $("qlangtech/plugins")//.shallExtractIssues()
            , $("qlangtech/ng-tis")
            , $("qlangtech/tis-ansible")
            , $("qlangtech/tis-git-manager")
            , $("qlangtech/update-center2")
            , $("qlangtech/DataX")
          //  , $("baisui1981/tis-logback-flume-appender")
            , $("qlangtech/flink", "tis-1.13.1")
            , $("qlangtech/chunjun", "tis-v1.12.5")
            , $("qlangtech/tis-doc")
            , $("qlangtech/hudi", "tis-release-0.10.1")
            , $("qlangtech/zeppelin", "tis-v0.10.1")};


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

        // final String tagName = "v3.6.0-alpha";
        System.out.println("Start Release Version:"+ GenerateChangList.tagName);
        TISVersion tagName = GenerateChangList.tagName;
        final String releaseBody = FileUtils.readFileToString(new File("release/" + tagName + ".md"), Charset.forName("utf8"));
        // GHRepository repo = github.getRepository("baisui1981/tisearch");
        System.out.println("1. Initialize");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.initialize(changList.getGithub(), tagName, releaseBody);
        }

        System.out.println("2. Create Tag");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.createTag(tagName);
        }

        System.out.println("3. Publish Release");
        for (TisRepo tisRepo : tisRelsRepo) {
            tisRepo.publishRelease();
        }
        System.out.println("Successful Release Version:"+ GenerateChangList.tagName);
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
