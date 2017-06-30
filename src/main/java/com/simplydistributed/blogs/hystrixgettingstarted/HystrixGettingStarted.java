package com.simplydistributed.blogs.hystrixgettingstarted;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

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

        public FailedCommand() {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        }

        @Override
        protected String run() {
            throw new RuntimeException("I just failed");
        }
    }
}
