package io.xsun.simpletreehole.android.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.xsun.simpletreehole.android.data.UserProfile;
import lombok.Getter;

public final class UserService {

    private static final UserService instance = new UserService();
    private final Map<String, UserProfile> emailMap = new HashMap<>();
    private final Map<Long, UserProfile> idMap = new HashMap<>();
    private final Map<String, String> authMap = new HashMap<>();

    private UserService() {
        for (int i = 0; i < 100; i++) {
            var profile = new UserProfile();
            profile.setId(i);
            profile.setNickname("User " + i);
            profile.setEmail("user" + i + "@example.com");
            emailMap.put(profile.getEmail(), profile);
            idMap.put(profile.getId(), profile);
            authMap.put(profile.getEmail(), "password");
        }
    }

    public static UserService getInstance() {
        return instance;
    }

    public static class DuplicateEmailException extends RuntimeException {
        @Getter
        private final String email;

        public DuplicateEmailException(String email) {
            super("Duplicate Email: " + email);
            this.email = email;
        }
    }

    public void register(String email, String nickname, String password, TaskRunner.Callback<Long> callback) {
        TaskRunner.getInstance().execute(() -> {
            if (emailMap.containsKey(email)) {
                throw new DuplicateEmailException(email);
            } else {
                authMap.put(email, password);
                var profile = new UserProfile();
                profile.setId(emailMap.size());
                profile.setEmail(email);
                profile.setNickname(nickname);
                emailMap.put(email, profile);
                idMap.put(profile.getId(), profile);
                return profile.getId();
            }
        }, callback);
    }

    public static class NoSuchEmailOrPasswordException extends RuntimeException {
        @Getter
        private final String email;

        public NoSuchEmailOrPasswordException(String email) {
            super("No such email or password: " + email);
            this.email = email;
        }
    }

    public void login(String email, String password, TaskRunner.Callback<Long> callback) {
        TaskRunner.getInstance().execute(() -> {
            if (Objects.equals(authMap.get(email), password)) {
                return Objects.requireNonNull(emailMap.get(email)).getId();
            } else {
                throw new NoSuchEmailOrPasswordException(email);
            }
        }, callback);
    }

    public void userProfile(Long id, TaskRunner.Callback<UserProfile> callback) {
        TaskRunner.getInstance().execute(() -> idMap.get(id), callback);
    }

    Map<String, UserProfile> getEmailMap() {
        return emailMap;
    }

    Map<Long, UserProfile> getIdMap() {
        return idMap;
    }

    Map<String, String> getAuthMap() {
        return authMap;
    }
}
