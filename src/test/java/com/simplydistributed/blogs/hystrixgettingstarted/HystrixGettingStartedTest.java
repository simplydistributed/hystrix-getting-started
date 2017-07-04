package com.simplydistributed.blogs.hystrixgettingstarted;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class HystrixGettingStartedTest
{
    @Before
    public void setUp() throws Exception
    {
        // Required to reset any open circuits
        Hystrix.reset(getDefaultHealthSnapshotIntervalMilliseconds(), TimeUnit.MILLISECONDS);
    }

    @Test
    public void SuccessfulCommand_returnsAString() throws Exception
    {
        HystrixGettingStarted.SuccessfulCommand command = new HystrixGettingStarted.SuccessfulCommand("variable");
        String result = command.execute();

        assertThat(result).isEqualTo("I ran successfully with param: variable");
    }

    @Test
    public void FailedCommand_throwsAnError() throws Exception
    {
        assertThatExceptionCauseMessageStartsWith(() -> new HystrixGettingStarted.FailedCommand().execute(), "I just failed");
        waitUntilHealthSnapshotFires();
        assertThatExceptionCauseMessageStartsWith(() -> new HystrixGettingStarted.FailedCommand().execute(), "Hystrix circuit short-circuited and is OPEN");
    }

    private void assertThatExceptionCauseMessageStartsWith(Runnable runnable, String message) throws InterruptedException
    {
        try {
            runnable.run();
        } catch(HystrixRuntimeException ex) {
            assertThat(ex.getCause()).isNotNull();
            assertThat(ex.getCause().getMessage()).startsWith(message);
        }
    }

    private void waitUntilHealthSnapshotFires() throws InterruptedException
    {
        // This is required because the requests fail so quickly that the health snapshot doesn't have time to check health.
        // https://github.com/Netflix/Hystrix/wiki/Configuration#metricshealthsnapshotintervalinmilliseconds
        Thread.sleep(getDefaultHealthSnapshotIntervalMilliseconds());
    }

    private Integer getDefaultHealthSnapshotIntervalMilliseconds()
    {
        HystrixCommandProperties defaultProperties =
            HystrixPropertiesFactory.getCommandProperties(HystrixCommandKey.Factory.asKey("FailedCommand"), null);
        return defaultProperties.metricsHealthSnapshotIntervalInMilliseconds().get();
    }

    @Test
    public void FailedCommand_requiresACertainIntervalBeforeOpeningCircuit() throws Exception
    {
        for(int i = 0; i < 100; i++) {
            assertThatExceptionCauseMessageStartsWith(() -> new HystrixGettingStarted.FailedCommand().execute(), "I just failed");
            // Uncomment this line to demonstrate that circuit will not open until the snapshot interval runs.
            // waitUntilHealthSnapshotFires();
        }
    }
}