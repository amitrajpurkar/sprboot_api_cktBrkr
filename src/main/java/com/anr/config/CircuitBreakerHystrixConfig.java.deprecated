package com.anr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * https://github.com/Netflix/Hystrix/wiki/Configuration
 * https://github.com/Netflix/Hystrix/wiki/How-To-Use
 * https://github.com/Netflix/Hystrix/wiki/How-it-Works
 * https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-javanica
 * https://github.com/Netflix/Hystrix/issues/1554
 *
 * https://spring.io/guides/gs/circuit-breaker/
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi__circuit_breaker_hystrix_clients.html
 * http://www.baeldung.com/spring-cloud-netflix-hystrix
 * https://www.credera.com/blog/credera-site/circuit-breaker-pattern-in-spring-boot/
 * https://steeltoe.io/docs/steeltoe-circuitbreaker/
 * https://www.programcreek.com/java-api-examples/?api=com.netflix.hystrix.contrib.javanica.anotation.HystrixProperty
 * https://www.ebayinc.com/stories/blogs/tech/application-resiliency-using-netflix-hystrix/
 * http://thegordian.blogspot.com/2015/07/a-good-thread-pool.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 * http://www.bigsoft.co.uk/blog/2009/11/27/rules-of-a-threadpoolexecutor-pool-size
 *
 *
 * @author amitr
 *
 */
@Configuration
public class CircuitBreakerHystrixConfig {
    @Autowired
    private ConfigProperties appProps;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#circuit-breaker
     *
     * @return
     */
    private HystrixCommandProperties.Setter getDefaultCmdProperties() {
        HystrixCommandProperties.Setter cmdProps = HystrixCommandProperties.Setter();

        // NOTE: below is the default value
        cmdProps.withExecutionTimeoutEnabled(true);

        // NOTE: we are setting strategy to Thread instead of Semaphore so that execution thread can
        // be cut
        cmdProps.withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD);

        // NOTE: this is the default value
        cmdProps.withExecutionIsolationThreadInterruptOnTimeout(true);

        // NOTE: the timeout value should be the only one we should configure
        // cmdProps.withExecutionTimeoutInMilliseconds(int value);

        // NOTE: next set of values can be defaulted across the application or we may choose
        // different threadpool settings per group

        cmdProps.withFallbackEnabled(true);
        cmdProps.withCircuitBreakerSleepWindowInMilliseconds(appProps.getWaitperiod().getHyxCbSleepWindowMS());
        cmdProps.withCircuitBreakerEnabled(true);
        cmdProps.withCircuitBreakerRequestVolumeThreshold(appProps.getWaitperiod().getHyxCbReqVolmThreshold());
        cmdProps.withCircuitBreakerErrorThresholdPercentage(appProps.getWaitperiod().getHyxCbErrThresholdPercentage());

        return cmdProps;
    }

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#threadpool-properties
     *
     * @return
     */
    private HystrixThreadPoolProperties.Setter getDefaultThreadPoolProperties() {
        HystrixThreadPoolProperties.Setter tpProps = HystrixThreadPoolProperties.Setter();

        // NOTE: this is the only one we need to set.. default value is 10
        tpProps.withCoreSize(appProps.getWaitperiod().getHyxThrdPoolCoreSize());

        // NOTE: we do not need to set MaxThreadSize.. this value is used only if we configure
        // allowMaximumSizeToDivergeFromCoreSize is set to true
        // tpProps.withMaximumSize(appProps.getWaitperiod().getHyxThrdPoolMaxSize());

        // NOTE: we set below property to -1 which means use SynchronousQueue; if we set a positve
        // value it uses LinkedBlockingQueue
        // https://stackoverflow.com/questions/8591610/when-should-i-use-synchronousqueue
        tpProps.withMaxQueueSize(appProps.getWaitperiod().getHyxThrdPoolMaxQueSize());

        // NOTE: we do not need next property since we use maxQueueSize == -1, the below property is
        // used with LinkedBlockingQueue when we set a posiive value
        tpProps.withQueueSizeRejectionThreshold(appProps.getWaitperiod().getHyxThrdPoolQueSizeRejThreshold());

        return tpProps;
    }

    private HystrixThreadPoolProperties.Setter getThreadPoolControllerCalls() {
        HystrixThreadPoolProperties.Setter tpProps = getDefaultThreadPoolProperties();
        tpProps.withCoreSize(appProps.getWaitperiod().getHyxThrdPoolCoreSizeApi());
        return tpProps;
    }

    private HystrixThreadPoolProperties.Setter getThreadPoolLoggerCalls() {
        return null;
    }

    private HystrixThreadPoolProperties.Setter getThreadPoolRepositoryCalls() {
        return null;
    }

    private HystrixThreadPoolProperties.Setter getThreadPoolBackendCalls() {
        return null;
    }

    @Bean(name = "CmdConfigTestMethod")
    public HystrixCommand.Setter getCmdConfigForTestMethod() {
        int timeoutMS = 200;
        // NOTE: group-key by default is the class-name having the methods carrying the hystrix
        // annotations
        String groupKey = "testGroup";
        // NOTE: key by default is the method-name annotated with HystrixCommand
        String key = "callSlowBackendKey";

        HystrixCommand.Setter config = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey));
        config = config.andCommandKey(HystrixCommandKey.Factory.asKey(key));

        HystrixCommandProperties.Setter commandProperties = getDefaultCmdProperties();
        // NOTE: timeout is the only value we configure
        commandProperties.withExecutionTimeoutInMilliseconds(timeoutMS);

        config.andCommandPropertiesDefaults(commandProperties);
        config.andThreadPoolPropertiesDefaults(getDefaultThreadPoolProperties());

        return config;
    }

    @Bean(name = "CmdConfigDefService")
    public HystrixCommand.Setter getCmdConfigForDefaultApi() {
        int timeoutMS = appProps.getWaitperiod().getApiDefaultService();
        String groupKey = appProps.getWaitperiod().getApiGroupKey();
        String key = appProps.getWaitperiod().getApiDefServiceKey();

        HystrixCommand.Setter config = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey));
        config = config.andCommandKey(HystrixCommandKey.Factory.asKey(key));

        HystrixCommandProperties.Setter commandProperties = getDefaultCmdProperties();
        commandProperties.withExecutionTimeoutInMilliseconds(timeoutMS);

        config.andCommandPropertiesDefaults(commandProperties);
        config.andThreadPoolPropertiesDefaults(getDefaultThreadPoolProperties());

        return config;
    }

}
