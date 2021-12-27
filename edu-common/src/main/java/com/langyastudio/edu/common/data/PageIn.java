package com.langyastudio.edu.common.data;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.util.*;


/**
 * 自定义分页封装
 *
 * @param <T>
 */
public class PageIn<T> implements IPage<T>
{
    private static final long serialVersionUID = 1L;

    protected long            offSet;
    protected long            pageSize;
    protected List<OrderItem> orders;

    protected long    total;
    protected List<T> list;

    public static <T> PageIn<T> of(long offSet, long pageSize)
    {
        return new PageIn<>(offSet, pageSize);
    }

    public PageIn(long offSet, long pageSize)
    {
        this(offSet, pageSize, null);
    }

    public PageIn(long offSet, long pageSize, List<OrderItem> orders)
    {
        if (offSet < 0L)
        {
            this.offSet = 0;
        }
        if (orders == null)
        {
            orders = new ArrayList();
        }

        this.offSet = offSet;
        this.pageSize = pageSize;
        this.orders = orders;
        this.list = Collections.emptyList();
    }

    @Override
    public List<T> getRecords()
    {
        return this.list;
    }

    @Override
    public PageIn<T> setRecords(List<T> records)
    {
        this.list = records;
        return this;
    }

    @Override
    public long getTotal()
    {
        return this.total;
    }

    @Override
    public PageIn<T> setTotal(long total)
    {
        this.total = total;
        return this;
    }

    @Override
    public long getSize()
    {
        return this.pageSize;
    }

    @Override
    public PageIn<T> setSize(long size)
    {
        this.pageSize = size;
        return this;
    }

    @Override
    public long offset()
    {
        return this.offSet;
    }

    @Override
    public List<OrderItem> orders()
    {
        return this.getOrders();
    }

    public List<OrderItem> getOrders()
    {
        return this.orders;
    }

    public void setOrders(final List<OrderItem> orders)
    {
        this.orders = orders;
    }

    @Override
    public Long maxLimit()
    {
        return 1000L;
    }

    @Override
    public long getCurrent()
    {
        return -1;
    }

    @Override
    public PageIn<T> setCurrent(long current)
    {
        return this;
    }
}
