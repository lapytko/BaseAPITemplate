package com.baseapi.controller.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String errorMessage;
    private boolean success;

    public ApiResponse<T> error(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.data = null;

        return this;
    }

    public ApiResponse<T>  success(T data) {
        this.success = true;
        this.data = data;
        this.errorMessage = null;

        return this;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + ReflectionToStringBuilder.toString(data, ToStringStyle.SHORT_PREFIX_STYLE) +
                ", errorMessage='" + errorMessage + '\'' +
                ", success=" + success +
                '}';
    }
}