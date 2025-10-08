package com.anr.config;

import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.PLACEHOLDER_ARGUMENTS;
import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.PLACEHOLDER_EXCEPTION;
import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.PLACEHOLDER_INVOCATION_TIME;
import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME;
import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.PLACEHOLDER_TARGET_CLASS_SHORT_NAME;

import java.util.concurrent.Executor;

import javax.servlet.Filter;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.anr.logging.model.SplunkLogRecord;
import com.google.gson.Gson;

@Configuration
@ComponentScan
@EnableWebMvc
public class MainConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext appContext;

    @Autowired
    private ConfigProperties appProps;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(false);
        return crlf;
    }

    @Bean
    public TraceInterceptor traceInterceptor() {
        TraceInterceptor traceInterceptor = new TraceInterceptor();

        String enterMessage = "\"name\": \"" + PLACEHOLDER_TARGET_CLASS_SHORT_NAME + "." + PLACEHOLDER_METHOD_NAME
                + "\", \"methodEntryValues\" : \"" + PLACEHOLDER_ARGUMENTS + "\"";
        String exitMessage = "\"name\": \"" + PLACEHOLDER_TARGET_CLASS_SHORT_NAME + "." + PLACEHOLDER_METHOD_NAME
                + "\", \"methodExitValues\" : \"" + PLACEHOLDER_ARGUMENTS + "\", \" timeTaken\" : \""
                + PLACEHOLDER_INVOCATION_TIME + " ms.\"";
        String exceptionMessage = "\"name\": \"" + PLACEHOLDER_TARGET_CLASS_SHORT_NAME + "." + PLACEHOLDER_METHOD_NAME
                + "\", \"exceptionEncountered\" : \"" + PLACEHOLDER_EXCEPTION + "\"";
        traceInterceptor.setEnterMessage(enterMessage);
        traceInterceptor.setExitMessage(exitMessage);
        traceInterceptor.setExceptionMessage(exceptionMessage);
        traceInterceptor.setHideProxyClassNames(true);

        return traceInterceptor;
    }

    @Bean
    public Advisor traceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public * com.anr.service..*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, traceInterceptor());
    }

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean<Filter> frBean = new FilterRegistrationBean<>();
        frBean.setFilter(filter);
        frBean.setEnabled(false);
        return frBean;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean(name = "SBThreadPool")
    public Executor SBLoggingThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appProps.getExecutor().getCorePoolSize());
        executor.setMaxPoolSize(appProps.getExecutor().getMaxPoolSize());
        executor.setQueueCapacity(appProps.getExecutor().getQueueCapacity());
        executor.setThreadNamePrefix(appProps.getExecutor().getThreadNamePrefix());
        executor.setDaemon(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        return executor;
    }

    /**
     * set from the application-environment, the properties from app-server, this can happen onetime
     * at application startup; The splunk event needs to be captured at each transaction
     *
     * @return
     */
    @Bean
    public SplunkLogRecord getSplunkLogRecord() {
        SplunkLogRecord rec = new SplunkLogRecord();
        rec.setEnv("local");
        rec.setReporter(null);
        rec.setReportType(null);
        rec.setComponent(null);
        rec.setHostname("localhost");

        return rec;
    }

}
