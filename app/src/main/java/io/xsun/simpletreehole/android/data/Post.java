package io.xsun.simpletreehole.android.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int senderId;
    private String senderName;
    private LocalDateTime createTime;
    private String content;
    private int likeCount;
}
