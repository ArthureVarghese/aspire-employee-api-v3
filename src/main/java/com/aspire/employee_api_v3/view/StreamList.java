package com.aspire.employee_api_v3.view;

import com.aspire.employee_api_v3.model.Stream;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StreamList {
    List<StreamDto> streams;

    public StreamList(List<Stream> streams) {
        ArrayList<StreamDto> streamList = new ArrayList<>();
        for (Stream stream : streams) {
            streamList.add(new StreamDto(stream.getId(),stream.getName(),stream.getAccountId()));
        }
        this.streams = streamList;
    }
}
