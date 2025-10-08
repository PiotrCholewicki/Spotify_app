package com.example.spotify_stats.api.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USER_DETAILS")
@Data
public class UserDetails implements Serializable {
    private static final long serialVersionUID = 3937414011943770889L;

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCESS_TOKEN", length = 2000)
    private String accessToken;

    @Column(name = "REFRESH_TOKEN", length = 2000)
    private String refreshToken;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "E_MAIL")
    private String email;


    public void setUserName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmailId(String email) {
        this.email = email;
    }
}