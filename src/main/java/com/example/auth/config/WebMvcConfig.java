package com.example.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 디렉토리 절대 경로 생성
        String absoluteUploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();

        log.info("정적 리소스(URL) 매핑 설정 - /uploads/** ===> {}", absoluteUploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absoluteUploadPath);
    }
}


// cursor ai, claude code

// 이걸 이용해서 프론트엔드 제작 가이드 문서를 만들어줘
// front-end-guide.md ---> 프론트엔드 프로젝트 폴더로 복사
// vscode 에서 ctrl+j 터미널 -> claude

// @front-end-guide.md 이걸 이용하여 사용자 프로필 수정 페이지를 만들어주고
// 로그인 사용자명에 버튼을 생성하여 해당 페이지로 이동하고 테스트할 수 있도록 해줘
