package com.langyastudio.edu.common.data.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;


/**
 * Request 请求数据复制
 * can replace by ContentCachingRequestWrapper
 */
public class RequestWrapper extends HttpServletRequestWrapper
{
    private final byte[]             body;
    private final ServletInputStream inputStream;
    private       BufferedReader     reader;

    public RequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);

        //读一次 然后缓存起来
        body = request.getInputStream().readAllBytes();;
        inputStream = new RequestCachingInputStream(body);
    }

    public byte[] getBody()
    {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        if (inputStream != null)
        {
            return inputStream;
        }
        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        if (reader == null)
        {
            reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
        }
        return reader;
    }

    //代理一下ServletInputStream 里面真是内容为当前缓存的bytes
    private static class RequestCachingInputStream extends ServletInputStream
    {
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes)
        {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException
        {
            return inputStream.read();
        }

        @Override
        public boolean isFinished()
        {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady()
        {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener)
        {
        }
    }

}
