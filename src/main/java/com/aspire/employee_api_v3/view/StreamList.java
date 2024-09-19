package com.aspire.employee_api_v3.view;

import com.aspire.employee_api_v3.model.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StreamList {
    List<Stream> streams;
}
