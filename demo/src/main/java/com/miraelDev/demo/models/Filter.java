package com.miraelDev.demo.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Filter {

    private String field;
    private QueryOperator operator;
    private String value;
    private List<String> values;//Used in case of IN operator

    @Builder
    public Filter(
            String field,
            QueryOperator operator,
            String value,
            List<String> values
    ) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

}
