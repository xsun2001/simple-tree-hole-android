package io.xsun.simpletreehole.android.data;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private long id;
    private UserProfile sender;
    private LocalDateTime createTime;
    private String content;
    private Post post;
    private Set<UserProfile> likers;
}
