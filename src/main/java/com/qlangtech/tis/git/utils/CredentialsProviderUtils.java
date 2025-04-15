//package com.qlangtech.tis.git.utils;
//
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
//import org.kohsuke.github.GitHubBuilder;
//import org.kohsuke.github.authorization.AuthorizationProvider;
//
//import java.lang.reflect.Field;
//
///**
// * @author: 百岁（baisui@qlangtech.com）
// * @create: 2025-04-14 22:02
// **/
//public class CredentialsProviderUtils {
//    public static UsernamePasswordCredentialsProvider getCredentials() throws Exception {
//        GitHubBuilder gitHubBuilder = GitHubBuilder.fromPropertyFile();
//
//        Field authorizationProvider = gitHubBuilder.getClass().getDeclaredField("authorizationProvider");
//        authorizationProvider.setAccessible(true);
//        AuthorizationProvider authProvider = (AuthorizationProvider) authorizationProvider.get(gitHubBuilder);
//
//        return new UsernamePasswordCredentialsProvider(
//                "your_github_username",
//                "0dd4180ad9d9eac5be7d6600dfbfcf48" //  authProvider.getEncodedAuthorization() // 建议使用 Token 而非密码
//        );
//    }
//
//    public static void main(String[] args) throws Exception {
//        UsernamePasswordCredentialsProvider credentials = CredentialsProviderUtils.getCredentials();
//
//        System.out.println(credentials);
//    }
//}
