package uk.co.davidcryer.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

public class WaitForJobsOnShutdownJobListener extends JobListenerSupport {
    private final Object shutdownLock = new Object();
    private int runningJobs = 0;
    private boolean shuttingDown = false;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        synchronized (shutdownLock) {
            ++runningJobs;
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        synchronized (shutdownLock) {
            --runningJobs;
            if (shuttingDown && runningJobs == 0) {
                shutdownLock.notifyAll();
            }
        }
    }

    public void waitOnShutdownLock() throws InterruptedException {
        synchronized (shutdownLock) {
            if (runningJobs == 0) {
                return;
            }
            shuttingDown = true;
            shutdownLock.wait();
        }
    }
}
