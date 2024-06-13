package com.baseapi.utils;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class CustomResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream captured;
    private ServletOutputStream output;


    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(byteArrayOutputStream);

    public CustomResponseWrapper(HttpServletResponse response) {
        super(response);
        captured = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (output == null) {
            output = new CaptureServletOutputStream(getResponse().getOutputStream());
        }

        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (output != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(captured, getResponse().getCharacterEncoding()));
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();

        if (writer != null) {
            writer.flush();
        } else if (output != null) {
            output.flush();
        }
    }

    public byte[] getCaptureAsBytes() throws IOException {
        if (writer != null) {
            writer.close();
        } else if (output != null) {
            output.close();
        }

        return captured.toByteArray();
    }

    public String getCaptureAsString() throws IOException {
        return new String(getCaptureAsBytes(), getResponse().getCharacterEncoding());
    }

    public String getResponseBody() throws UnsupportedEncodingException {
        writer.flush();
        return new String(byteArrayOutputStream.toByteArray(), getCharacterEncoding());
    }

    private class CaptureServletOutputStream extends ServletOutputStream {

        private ServletOutputStream outputStream;
        private ByteArrayOutputStream capture;

        public CaptureServletOutputStream(ServletOutputStream outputStream) {
            this.outputStream = outputStream;
            this.capture = new ByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            capture.write(b);
        }

        @Override
        public void flush() throws IOException {
            outputStream.flush();
            capture.flush();
        }

        @Override
        public void close() throws IOException {
            outputStream.close();
            capture.close();
        }

        public byte[] getCaptureAsBytes() {
            return capture.toByteArray();
        }

        public String getCaptureAsString() throws IOException {
            return new String(getCaptureAsBytes(), getResponse().getCharacterEncoding());
        }

        @Override
        public boolean isReady() {
            return true; // Возвращает true, если поток готов к дальнейшей записи
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // Этот метод используется для асинхронной записи,
            // но в данном случае мы его не реализуем.
            throw new UnsupportedOperationException("Not implemented");
        }

        public PrintWriter getWriter() throws IOException {
            return writer;
        }

    }
}

