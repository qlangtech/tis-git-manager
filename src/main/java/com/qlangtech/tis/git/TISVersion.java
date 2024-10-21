package com.qlangtech.tis.git;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-20 18:02
 **/
public class TISVersion {
    private static final Pattern PATTERN_VER = Pattern.compile("[vV](.+)");
    private final String version;
    public final String versionNum;
    public static final Charset utf8 = Charset.forName("utf8");
    private final String releaseVersion;

    /**
     * 每次发布的预发布版本，alpha、beta，rc1 等等
     */
    private final Optional<String> candidateVerion;

    private File getReleaseNoteFile() {
        File releaseNoteFile = new File("release/" + this.version + ".md");
        return releaseNoteFile;
    }

    public String getReleaseBody() {
        try {
            final String releaseBody = FileUtils.readFileToString(this.getReleaseNoteFile(), utf8);
            return releaseBody;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeReleaseNote(StringBuffer changeListContent) throws IOException {
        StringBuffer content = new StringBuffer();

        File abstractFile = new File("release/" + this.releaseVersion + "-abstract.md");
        if (abstractFile.exists()) {
            content.append("## Abstract\n");
            content.append(FileUtils.readFileToString(abstractFile, utf8)).append("\n");
        }

        content.append(changeListContent);

        FileUtils.writeStringToFile(this.getReleaseNoteFile(), content.toString(), utf8, false);
    }

    public TISVersion(String version) {
        this(version, Optional.empty());
    }

    public TISVersion(String version, Optional<String> candidateVerion) {
        this.version = version;
        Matcher m = PATTERN_VER.matcher(version);
        if (!m.matches()) {
            throw new IllegalArgumentException("version:" + version + " is not match pattern:" + PATTERN_VER);
        }
        this.versionNum = m.group(1);
        final String[] split = StringUtils.split(version, "-");
        if (split.length > 1) {
            releaseVersion = split[0];
        } else {
            releaseVersion = version;
        }
        this.candidateVerion = candidateVerion;
    }

    public final String getVersion() {
        return this.version;
    }

    public final String getFullVersion() {
        return this.version + this.candidateVerion.map((c) -> "-" + c).orElse(StringUtils.EMPTY);
    }

    @Override
    public String toString() {
        return this.version;
    }
}
