
## 生成本地Release文件

com.qlangtech.tis.git.GenerateChangList

mvn compile exec:java -Dexec.mainClass=com.qlangtech.tis.git.GenerateChangList


## Github生成Release

com.qlangtech.tis.git.Main

## 更新本地pom 版本，并且push内容到git仓库中

com.qlangtech.tis.git.ChangVersionAndPush

