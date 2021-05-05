package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ShutdownJobListener extends JobListenerSupport {
    private final Object shutdownLock = new Object();
    private final AtomicInteger runningJobs = new AtomicInteger(0);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        runningJobs.incrementAndGet();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        runningJobs.decrementAndGet();
        if (shuttingDown.get() && runningJobs.get() == 0) {
            synchronized (shutdownLock) {
                shutdownLock.notifyAll();
            }
        }
    }

    public void waitOnShutdownLock() throws InterruptedException {
        if (runningJobs.incrementAndGet() == 0) {
            return;
        }
        shuttingDown.set(true);
        synchronized (shutdownLock) {
            shutdownLock.wait();
        }
    }
}
