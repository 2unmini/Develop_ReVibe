package com.doubleowner.revibe.domain.user.entity;

import com.doubleowner.revibe.domain.account.entity.Account;
import com.doubleowner.revibe.domain.chat.entity.UserChat;
import com.doubleowner.revibe.domain.coupon.entity.IssuedCoupon;
import com.doubleowner.revibe.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.doubleowner.revibe.global.common.BaseTimeEntity;
import com.doubleowner.revibe.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

import static com.doubleowner.revibe.global.exception.errorCode.ErrorCode.INSUFFICIENT_POINTS;

@Entity
@Getter
@Table
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus status = UserStatus.USER_ACTIVE;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LoginMethod loginMethod = LoginMethod.LOCAL;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int point;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuedCoupon> issuedCoupons;

    @OneToMany(mappedBy = "user")
    private List<UserChat> userChats;

    public User() {
    }

    public User(String email, String nickname, String password ,String address, String phoneNumber) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        role=Role.ROLE_USER;
    }

    public void deletedUser() {
        this.status = UserStatus.USER_DELETED;
    }


    public void updateProfile(UserProfileUpdateRequestDto requestDto, String password) {
        this.nickname = requestDto.getNickname();
        this.password = password;
        /*this.profileImage = requestDto.getProfileImage().toString();*/
        this.address = requestDto.getAddress();
        this.phoneNumber = requestDto.getPhoneNumber();
    }

    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.password = "qwer";
        this.role = Role.ROLE_USER;
        this.status = UserStatus.USER_ACTIVE;
        this.loginMethod = LoginMethod.KAKAO;
    }

    public void addPoint(int point) {
        this.point += point;
    }

    public void minusPoint(int point) {
        if (this.point - point < 0) {
            throw new CustomException(INSUFFICIENT_POINTS);
        }
        this.point -= point;
    }

}