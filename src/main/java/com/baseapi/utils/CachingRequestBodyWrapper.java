package com.baseapi.utils;



import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CachingRequestBodyWrapper extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedBytes;

    public CachingRequestBodyWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cacheInputStream(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream();
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream(InputStream inputStream) throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, cachedBytes);
    }

    public class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream input;

        public CachedServletInputStream() {
            input = new ByteArrayInputStream(cachedBytes.toByteArray());
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    public String getBody() {
        return cachedBytes.toString(StandardCharsets.UTF_8);
    }
}



