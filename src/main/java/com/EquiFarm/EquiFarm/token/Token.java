package com.EquiFarm.EquiFarm.token;

import com.EquiFarm.EquiFarm.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Entity
// public class Token {
//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     @Column(name = "id", updatable = false)
//     private Long id;

//     @Column(unique = true)
//     public String token;

//     @Enumerated(EnumType.STRING)
//     public TokenType tokenType = TokenType.BEARER;
//     public boolean revoked;
//     public boolean expired;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id")
//     public User user;

// }

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

}