package com.simplydistributed.blogs.hystrixgettingstarted;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HystrixGettingStarted
{
    public static class SuccessfulCommand extends HystrixCommand<String> {

        private final String param;

        public SuccessfulCommand(String param) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.param = param;
        }

        @Override
        protected String run() {
            return String.format("I ran successfully with param: %s", param);
        }
    }

    public static class FailedCommand extends HystrixCommand<String> {
        private static int count = 0;

        public FailedCommand() {

            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withCircuitBreakerRequestVolumeThreshold(1)
                ));
        }

        @Override
        protected String run() {
            System.out.println("Running FailedCommand: " + count);
            throw new RuntimeException("I just failed: " + count++);
        }
    }
}
