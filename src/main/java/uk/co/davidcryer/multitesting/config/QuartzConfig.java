package uk.co.davidcryer.multitesting.config;

import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import uk.co.davidcryer.multitesting.cv.*;
import uk.co.davidcryer.quartz.DeleteOldJobsListener;
import uk.co.davidcryer.quartz.WaitForJobsOnShutdownConfig;
import uk.co.davidcryer.quartz.WaitForJobsOnShutdownJobListener;

import java.util.Properties;
import java.util.Set;

@Configuration
@Import(WaitForJobsOnShutdownConfig.class)
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean quartzScheduler(ApplicationContext applicationContext,
                                                JobDetail[] jobDetails,
                                                JobListener[] jobListeners) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setOverwriteExistingJobs(false);
        schedulerFactoryBean.setQuartzProperties(getQuartzProperties());
        schedulerFactoryBean.setJobDetails(jobDetails);
        schedulerFactoryBean.setGlobalJobListeners(jobListeners);
        return schedulerFactoryBean;
    }

    @SneakyThrows
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean, Set<String> jobsRequiringDeletion) {
        var scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(new DeleteOldJobsListener(scheduler, jobsRequiringDeletion));
        return scheduler;
    }

    @Bean
    public Set<String> jobsRequiringDeletion() {
        return Set.of(
                SaveCvOrchestratorJob.KEY,
                PublishCvTaskJob.KEY,
                PublishCvToKafkaConcurrentTasksJob.KEY
        );
    }

    @Bean
    public JobDetail[] jobDetails() {
        return new JobDetail[] {
                jobDetail(StoreCvTaskJob.class, StoreCvTaskJob.KEY),
                jobDetail(PublishCvToClientTaskJob.class, PublishCvToClientTaskJob.KEY),
                jobDetail(PublishCvToKafkaTaskJob.class, PublishCvToKafkaTaskJob.KEY),
                jobDetail(UpdateCvWithPublishStatusTaskJob.class, UpdateCvWithPublishStatusTaskJob.KEY),
                jobDetail(NoOpJob.class, NoOpJob.KEY)
        };
    }

    @Bean
    public JobListener[] jobListeners(WaitForJobsOnShutdownJobListener waitForJobsOnShutdownJobListener) {
        return new JobListener[] {
                waitForJobsOnShutdownJobListener
        };
    }

    private JobDetail jobDetail(Class<? extends Job> clazz, String key) {
        return JobBuilder.newJob(clazz).withIdentity(key).storeDurably().build();
    }

    private Properties getQuartzProperties() {//TODO drive from DataSource bean and application.properties
        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "1");

        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "qrtz_");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        properties.setProperty("org.quartz.jobStore.dataSource", "qzDS");
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");

        properties.setProperty("org.quartz.dataSource.qzDS.driver", "org.postgresql.Driver");
        properties.setProperty("org.quartz.dataSource.qzDS.URL", "jdbc:postgresql://localhost:5432/demo?schema=demo");
        properties.setProperty("org.quartz.dataSource.qzDS.user", "demo");
        properties.setProperty("org.quartz.dataSource.qzDS.password", "password");
        properties.setProperty("org.quartz.dataSource.qzDS.provider", "hikaricp");

        properties.setProperty("org.quartz.scheduler.instanceName", "SyncScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");

        return properties;
    }

}
