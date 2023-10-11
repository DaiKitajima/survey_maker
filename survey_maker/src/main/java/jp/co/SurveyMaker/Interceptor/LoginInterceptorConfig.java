package jp.co.SurveyMaker.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer  {

    // InterceptorのDI
    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * Interceptorの配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	InterceptorRegistration registration  = registry.addInterceptor(loginInterceptor);
		// .addPathPatterns() フィルタのパスを設定
    	registration.addPathPatterns("/**");
		// .excludePathPatterns() フィルタ不要のパスを設定
    	registration.excludePathPatterns("/");
    	registration.excludePathPatterns("/login");
    	registration.excludePathPatterns("/logout");
    	registration.excludePathPatterns("/api/**");
    	registration.excludePathPatterns("/libs/**");
    	registration.excludePathPatterns("/favicon.ico");
    	registration.excludePathPatterns("/**/*.js");
    	registration.excludePathPatterns("/**/*.css");
    }
}