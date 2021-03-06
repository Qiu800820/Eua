package com.pocoin.basemvp.domain;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Robert yao on 2016/10/17.
 */

public class JobExecutor implements Executor {

    private static JobExecutor INSTANCE = null;

    public static JobExecutor getInstance(){
        if (null == INSTANCE ){
            INSTANCE = new JobExecutor();
        }
        return INSTANCE;
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private final BlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ThreadFactory threadFactory;

    public JobExecutor() {
        this.workQueue = new LinkedBlockingQueue<>(128);
        this.threadFactory = new JobThreadFactory();
        Log.d("JobExecutor", String.format("=== 初始化线程池 core_pool_size:%s, MAXIMUM_POOL_SIZE:%s===", CORE_POOL_SIZE, MAXIMUM_POOL_SIZE));
        this.threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.MILLISECONDS, this.workQueue, this.threadFactory);
    }

    @Override
    public void execute(Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "yjx_io_scheduler_";
        private int counter = 0;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter);
        }
    }

}
