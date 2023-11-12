package org.arni.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class GitUserDTO {
    @JsonProperty
    private String id;
    @JsonProperty
    private String login;
    @JsonProperty
    private String name;
    @JsonProperty
    private String type;
    @JsonProperty
    private String avatarUrl;
    @JsonProperty
    private String createdAt;
    @JsonProperty
    private String calculations;

    public static GitUserDTO buildFromGitUser(GitUser user) {
        return GitUserDTO.builder()
                .id(String.valueOf(user.getId()))
                .login(user.getLogin())
                .name(user.getName())
                .type(user.getType())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .calculations(user.getCalculations().toString()).build();
    }
}
