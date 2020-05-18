package com.zuul.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by zhiwen on 2017/8/4.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${info.app.name}")
    private String serviceName;
    @Value("${info.app.desc}")
    private String serviceDesc;
    @Value("${security.oauth2.client.client-id}")
    String clientId;
    @Value("${security.oauth2.client.client-secret}")
    String clientSecret;
    private static final String TOKEN_URL = "authentication/oauth/token";
//    @Bean
//    public Docket postsApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(Predicates.not(RequestHandlerSelectors.basePackage("com.zuul")))
//                .paths(PathSelectors.any())
//                .build()
//                .securitySchemes(Lists.newArrayList(oauth()))
//                .securityContexts(Lists.newArrayList(securityContext()))
//                ;
//    }
//
//    private Predicate<String> postPaths() {
//        return regex("/*");
//    }
//
//    private Predicate<String> springBootActuatorJmxPaths() {
//        return regex("^/(?!env|restart|pause|resume|refresh).*$");
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder().title(serviceName).description(serviceDesc).build();
//    }
//
//    @Bean
//    List<GrantType> grantTypes() {
//        String tokenUrl = "authentication-center-zz/oauth/token", authorizeUrl = "authentication-center-zz/oauth/authorize", loginUrl = "/login";
//        List<GrantType> grantTypes = new ArrayList<>();
//        ClientCredentialsGrant clientCredentialsGrant = new ClientCredentialsGrant(tokenUrl);
//        ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant =
//                new ResourceOwnerPasswordCredentialsGrant(tokenUrl);
//        AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(new TokenRequestEndpoint(authorizeUrl
//                , "clientId", "clientSecret"), new TokenEndpoint(tokenUrl, "access_token"));
//        ImplicitGrant implicitGrant = new ImplicitGrant(new LoginEndpoint(tokenUrl), "access_token");
//        grantTypes.add(resourceOwnerPasswordCredentialsGrant);
//        grantTypes.add(implicitGrant);
//        grantTypes.add(authorizationCodeGrant);
//        grantTypes.add(clientCredentialsGrant);
//        return grantTypes;
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.ant("/api/**"))//配置哪些url需要做oauth2认证
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        return Lists.newArrayList(
//                new SecurityReference("oauth2", scopes().toArray(new AuthorizationScope[0])));
//    }
//
//    SecurityScheme oauth() {
//        return new OAuthBuilder()
//                .name("oauth2")
//                .scopes(scopes())
//                .grantTypes(grantTypes())
//                .build();
//    }
//
//    private List<AuthorizationScope> scopes() {
//        List<AuthorizationScope> list = new ArrayList();
//        list.add(new AuthorizationScope("read", "Grants read access"));
//        list.add(new AuthorizationScope("write", "Grants write access"));
//        list.add(new AuthorizationScope("all", "Grants all access"));
//        return list;
//    }
//
//    @Bean
//    public SecurityConfiguration securityInfo() {
//        return new SecurityConfiguration(clientId, clientSecret, "realm",
//                "auth-server", "access_token", ApiKeyVehicle.HEADER, "access_token", ",");
//    }
    //这个东西是项目的根路径，也就是“/oauth/token”前面的那一串
    //这个东西在配置文件里写的，大家也可以直接写死在配置文件中

    /**
     * 主要是这个方法，其他的方法是抽出去的，所以大家不要害怕为啥有这么多方法
     * 在 basePackage 里面写需要生成文档的 controller 路径
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ybk.ordering.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    /**
     * 这个方法主要是写一些文档的描述
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "某某系统API",
                "This is a very pretty document!",
                "1.0",
                "",
                new Contact("师父领进门", "", "qixiazhen@qq.com"),
                "", "", Collections.emptyList());
    }

    /**
     * 这个类决定了你使用哪种认证方式，我这里使用密码模式
     * 其他方式自己摸索一下，完全莫问题啊
     */
    private SecurityScheme securityScheme() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant(TOKEN_URL);

        return new OAuthBuilder()
                .name("spring_oauth")
                .grantTypes(Collections.singletonList(grantType))
                .scopes(Arrays.asList(scopes()))
                .build();
    }

    /**
     * 这里设置 swagger2 认证的安全上下文
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("spring_oauth", scopes())))
                .forPaths(PathSelectors.any())
                .build();
    }

    /**
     * 这里是写允许认证的scope
     */
    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("all", "All scope is trusted!")
        };
    }

}