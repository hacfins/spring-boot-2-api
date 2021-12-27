package com.langyastudio.edu.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import java.io.*;
import java.lang.reflect.Method;

/**
 * @author MrBird
 */
@Log4j2
public class AddressT
{
    @SuppressWarnings("all")
    public static String getCityInfo(String ip)
    {
        if(NetUtil.isInnerIP(ip))
        {
            return "";
        }

        DbSearcher searcher = null;
        try
        {
            String dbPath = AddressT.class.getResource("/ip2region/ip2region.db").getPath();
            File   file   = new File(dbPath);
            if (!file.exists())
            {
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                dbPath = tmpDir + "ip.db";

                file = new File(dbPath);
                @Cleanup OutputStream outputStream = new FileOutputStream(file);
                @Cleanup InputStream inputStream = AddressT.class.getClassLoader()
                        .getResourceAsStream("classpath:ip2region/ip2region.db");

                IoUtil.copy(inputStream, outputStream);
            }

            int      algorithm = DbSearcher.BTREE_ALGORITHM;
            DbConfig config    = new DbConfig();
            searcher = new DbSearcher(config, dbPath);
            Method method = null;
            switch (algorithm)
            {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
            }

            DataBlock dataBlock = null;
            if (!Util.isIpAddress(ip))
            {
                log.error("Error: Invalid ip address");
            }
            dataBlock = (DataBlock) method.invoke(searcher, ip);

            return dataBlock.getRegion();
        }
        catch (Exception e)
        {
            log.error("获取IP地址失败，{}", e.getMessage());
        }
        finally
        {
            if (searcher != null)
            {
                try
                {
                    searcher.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
