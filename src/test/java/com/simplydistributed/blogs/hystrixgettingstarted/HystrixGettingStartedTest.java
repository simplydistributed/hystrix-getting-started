package com.simplydistributed.blogs.hystrixgettingstarted;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HystrixGettingStartedTest
{
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
        try {
            new HystrixGettingStarted.FailedCommand().execute();
        } catch(HystrixRuntimeException ex) {
            assertThat(ex.getCause()).isNotNull();
            assertThat(ex.getCause().getMessage()).isEqualTo("I just failed");
        }
    }
}