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
public class ApiRequest<T> {
    private T data;


    @Override
    public String toString() {
        return "ApiQuest{" +
                "data=" + ReflectionToStringBuilder.toString(data, ToStringStyle.SHORT_PREFIX_STYLE) +
                '}';
    }
}