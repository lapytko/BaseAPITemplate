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
public class SaveResponse {
    private String data;
    private boolean isNew;


    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + ReflectionToStringBuilder.toString(data, ToStringStyle.SHORT_PREFIX_STYLE) +
                ", isNew=" + isNew +
                '}';
    }
}