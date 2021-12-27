package com.langyastudio.edu.common.util;

import java.util.concurrent.*;

/**
 * 线程池
 */
public class ThreadPoolT
{

    /**
     * 创建线程池
     *
     * @param corePoolSize            核心线程数
     * @param maximumPoolSize         最大线程数
     * @param keepAliveMs             最大线程数可以存活的时间（毫秒）
     * @param linkedBlockingQueueSize 可等待执行任务的数量
     * @param handler                 拒绝策略
     *
     * @return ThreadPoolExecutor
     */
    private static ThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveMs,
                                             int linkedBlockingQueueSize, ThreadPoolExecutor.AbortPolicy handler)
    {

        return new ThreadPoolExecutor(corePoolSize,
                                      maximumPoolSize,
                                      keepAliveMs,
                                      TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<>(linkedBlockingQueueSize),
                                      Executors.defaultThreadFactory(),
                                      handler);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize            核心线程数
     * @param maximumPoolSize         最大线程数
     * @param keepAliveMs             最大线程数可以存活的时间（毫秒）
     * @param linkedBlockingQueueSize 可等待执行任务的数量
     *
     * @return ThreadPoolExecutor
     */
    private static ThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveMs,
                                             int linkedBlockingQueueSize)
    {

        return new ThreadPoolExecutor(corePoolSize,
                                      maximumPoolSize,
                                      keepAliveMs,
                                      TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<>(linkedBlockingQueueSize),
                                      Executors.defaultThreadFactory(),
                                      new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 创建线程池
     * 无缓冲
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     *
     * @return ThreadPoolExecutor
     */
    private static ThreadPoolExecutor create(int corePoolSize, int maximumPoolSize)
    {

        return new ThreadPoolExecutor(corePoolSize,
                                      maximumPoolSize,
                                      1000 * 100,
                                      TimeUnit.MILLISECONDS,
                                      new SynchronousQueue<>(),
                                      Executors.defaultThreadFactory(),
                                      new ThreadPoolExecutor.AbortPolicy());
    }


    //    //7 ThreadPoolExecutor：最原始的创建线程池的方式，它包含了 7 个参数可供设置，后面会详细讲。
    //    int corePoolSize = 1; // 核心线程数，线程池中始终存活的线程数
    //    int maximumPoolSize = 1;// 最大线程数，线程池中允许的最大线程数，当线程池的任务队列满了之后可以创建的最大线程数
    //    int keepAliveTime = 100;// 最大线程数可以存活的时间，当线程中没有任务执行时，最大线程就会销毁一部分，最终保持核心线程数量的线程
    //    TimeUnit unit = TimeUnit.SECONDS;// 最大线程数可以存活的时间的单位
    //    /**
    //     * 一个阻塞队列，用来存储线程池等待执行的任务，均为线程安全，包含 7 种类型:
    //     * ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
    //     * LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
    //     * SynchronousQueue：一个不存储元素的阻塞队列，即直接提交给线程不保持它们。
    //     * PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
    //     * DelayQueue：一个使用优先级队列实现的无界阻塞队列，只有在延迟期满时才能从中提取元素。
    //     * LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。与SynchronousQueue类似，还含有非阻塞方法。
    //     * LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
    //     * 较常用的是 LinkedBlockingQueue 和 Synchronous，线程池的排队策略与 BlockingQueue 有关
    //     */
    //    LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1);
    //    ThreadFactory threadFactory = Executors.defaultThreadFactory(); // 线程工厂，主要用来创建线程，默认为正常优先级、非守护线程
    //    /**
    //     * 拒绝策略，拒绝处理任务时的策略:
    //     * AbortPolicy：拒绝并抛出异常。
    //     * CallerRunsPolicy：使用当前调用的线程来执行此任务。
    //     * DiscardOldestPolicy：抛弃队列头部（最旧）的一个任务，并执行当前任务。
    //     * DiscardPolicy：忽略并抛弃当前任务。
    //     * 默认策略为 AbortPolicy。
    //     */
    //    //ThreadPoolExecutor.AbortPolicy handler = new ThreadPoolExecutor.AbortPolicy();
    //    ThreadPoolExecutor.AbortPolicy handler = new ThreadPoolExecutor.AbortPolicy() {
    //        @Override
    //        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    //            // 执行自定义拒绝策略的相关操作
    //            System.out.println("我是自定义拒绝策略~");
    //            r.run();
    //        }
    //    };
    //    /**
    //     * ThreadPoolExecutor 关键节点的执行流程如下：
    //     * 当线程数小于核心线程数时，创建线程。
    //     * 当线程数大于等于核心线程数，且任务队列未满时，将任务放入任务队列。
    //     * 当线程数大于等于核心线程数，且任务队列已满：若线程数小于最大线程数，创建线程；若线程数等于最大线程数，抛出异常，拒绝任务。
    //     */
    //    ThreadPoolExecutor threadPool
    //            = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler
    //    );
}
