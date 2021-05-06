package uk.co.davidcryer.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitForJobsOnShutdownJobListener extends JobListenerSupport {
    private final Object shutdownLock = new Object();
    private final AtomicInteger runningJobs = new AtomicInteger(0);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        synchronized (shutdownLock) {
            runningJobs.incrementAndGet();
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        synchronized (shutdownLock) {
            runningJobs.decrementAndGet();
            if (shuttingDown.get() && runningJobs.get() == 0) {
                shutdownLock.notifyAll();
            }
        }
    }

    public void waitOnShutdownLock() throws InterruptedException {
        synchronized (shutdownLock) {
            if (runningJobs.get() == 0) {
                return;
            }
            shuttingDown.set(true);
            shutdownLock.wait();
        }
    }
}
