package uk.co.davidcryer.multitesting.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean quartzScheduler(ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactoryBean.setOverwriteExistingJobs(false);
        schedulerFactoryBean.setQuartzProperties(getQuartzProperties());
        return schedulerFactoryBean;
    }

    private Properties getQuartzProperties() {//TODO drive from DataSource bean and application.properties
        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "1");

        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "vds.qrtz_");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        properties.setProperty("org.quartz.jobStore.dataSource", "qzDS");
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");

        properties.setProperty("org.quartz.dataSource.qzDS.driver", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        properties.setProperty("org.quartz.dataSource.qzDS.URL", "jdbc:postgresql://localhost:5432/demo?schema=demo");
        properties.setProperty("org.quartz.dataSource.qzDS.user", "demo");
        properties.setProperty("org.quartz.dataSource.qzDS.password", "password");
        properties.setProperty("org.quartz.dataSource.qzDS.provider", "hikaricp");

        properties.setProperty("org.quartz.scheduler.instanceName", "SyncScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");

        return properties;
    }

}
