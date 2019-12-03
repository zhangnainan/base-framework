package com.sg.base.dao;

/**
 * Transaction
 *
 * @author Dai Wenqing
 * @date 2016/2/26
 */
public interface Transaction {
    /**
     * 开始事务控制。 开始后当前线程通过get方法请求的Connection均为同一个，并且为可读写连接实例。
     * 也可以通过在方法上添加@javax.transaction.Transactional注解来开启。
     * 事务会在rollback或close方法被调用时，自动提交并结束。
     */
    void beginTransaction();

    /**
     * 判断是否已经开启了事务
     *
     * @return
     */
    boolean hasBeginTransaction();

    /**
     * 事务是不是已经提交了
     *
     * @return
     */
    boolean isCommit();

    void commit();

    void rollback();
}
