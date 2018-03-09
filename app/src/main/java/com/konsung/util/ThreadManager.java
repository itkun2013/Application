package com.konsung.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类功能： 线程池管理类
 */
public class ThreadManager {

    private static ThreadManager instance = new ThreadManager();
    private ThreadPoolProxy longPool;
    private ThreadPoolProxy shortPool;
    //开启线程数量
    private final int longThreadNum = 5;
    private final int shortThreadNum = 3;
    //线程开启最大数量
    private final int longThreadMaxNum = 5;
    private final int shortThreadMaxNum = 3;
    //线程超时时长
    private final long threadOvertime = 3000L;
    //队列数量
    private final int queueNum = 10;
    /**
     * 单例
     * @return ThreadManager
     */
    public static ThreadManager getInstance() {
        return instance;
    }

    /**
     * 联网比较耗时
     * @return ThreadPoolProxy
     */
    public synchronized ThreadPoolProxy createLongPool() {
        if (longPool == null) {
            longPool = new ThreadPoolProxy(longThreadNum, longThreadMaxNum, threadOvertime);
        }
        return longPool;
    }

    /**
     * 操作本地文件
     * @return ThreadPoolProxy
     */
    public synchronized ThreadPoolProxy createShortPool() {
        if (shortPool == null) {
            shortPool = new ThreadPoolProxy(shortThreadNum, shortThreadMaxNum, threadOvertime);
        }
        return shortPool;
    }

    /**
     * 线程配置类
     */
    public class ThreadPoolProxy {
        private ThreadPoolExecutor pool;
        private int corePoolSize;
        private int maximumPoolSize;
        private long time;

        /**
         * 构造器
         * @param corePoolSize 线程池中核心线程的数量
         * @param maximumPoolSize 线程池中最大线程数量
         * @param time 线程的超时时长
         */
        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.time = time;
        }

        /**
         * 执行任务
         * @param runnable runnable
         */
        public void execute(Runnable runnable) {
            if (pool == null) {
                // 创建线程池
                /*
                 * 1. 线程池里面管理多少个线程2. 如果排队满了, 额外的开的线程数3. 如果线程池没有要执行的任务 存活多久4.
                 * 时间的单位 5 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到LinkedBlockingQueue对象中 排队
                 */
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                        time, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(queueNum));
            }
            //允许线程超时(超时后闲置状态下的子线程会被回收，释放资源)
            pool.allowCoreThreadTimeOut(true);
            pool.execute(runnable); // 调用线程池 执行异步任务
        }

        /**
         * 取消任务
         * @param runnable runnable
         */
        public void cancel(Runnable runnable) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(runnable); // 取消异步任务
            }
        }
    }
}
