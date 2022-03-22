package com.newcoder.community.config;

import com.newcoder.community.Quartz.DelShareJob;
import com.newcoder.community.Quartz.ScoreJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author 86156
 */
@Configuration
public class QuartzConfig  {
    @Bean(name = "scoreJob")
    public JobDetailFactoryBean jobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(ScoreJob.class);
        jobDetailFactoryBean.setBeanName("ScoreJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

    @Bean(name = "delJob")
    public JobDetailFactoryBean delJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(DelShareJob.class);
        jobDetailFactoryBean.setBeanName("delShareJob");
        jobDetailFactoryBean.setGroup("communityJobGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean setSimpleTriggerFactoryBean(JobDetail scoreJob){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(scoreJob);
        factoryBean.setName("scoreJobTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000*60*5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
    @Bean
    public SimpleTriggerFactoryBean SimpleTriggerFactoryBean(JobDetail delJob){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(delJob);
        factoryBean.setName("scoreJobTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }



}
