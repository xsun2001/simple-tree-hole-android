package io.xsun.simpletreehole.android.data;

import lombok.Data;

@Data
public class Result {
    private int returnCode, newId;
    private String error;
}
