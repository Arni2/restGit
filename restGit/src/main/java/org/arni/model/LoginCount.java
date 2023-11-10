package org.arni.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "login_count")
public class LoginCount {

    @Id
    private long id;

    @JsonProperty("login")
    @Column(name = "LOGIN")
    private String login;
    @Column(name = "REQUEST_COUNT")
    private long requestCount;

    @Version
    private Integer version;
}
