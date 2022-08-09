package io.xsun.simpletreehole.android.data;

import java.util.Objects;

import lombok.Data;

@Data
public class UserProfile {
    private long id;
    private String email, nickname;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
