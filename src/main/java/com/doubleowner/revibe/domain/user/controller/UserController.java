package com.doubleowner.revibe.domain.user.controller;

import com.doubleowner.revibe.domain.user.dto.request.UserDeleteRequestDto;
import com.doubleowner.revibe.domain.user.dto.request.UserLoginRequestDto;
import com.doubleowner.revibe.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.doubleowner.revibe.domain.user.dto.request.UserSignupRequestDto;
import com.doubleowner.revibe.domain.user.dto.response.UserProfileResponseDto;
import com.doubleowner.revibe.domain.user.dto.response.UserSignupResponseDto;
import com.doubleowner.revibe.domain.user.service.UserService;
import com.doubleowner.revibe.global.common.dto.CommonResponseBody;
import com.doubleowner.revibe.global.config.auth.UserDetailsImpl;
import com.doubleowner.revibe.global.config.dto.JwtAuthResponse;
import com.doubleowner.revibe.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import static com.doubleowner.revibe.global.exception.errorCode.ErrorCode.FORBIDDEN_ACCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "회원 관련 API")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * @param requestDto - 회원가입을 위한 요청 정보
     * @return UserSignupResponseDto - 회원가입 완료 응답 dto
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원 가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공.")
    })
    public ResponseEntity<CommonResponseBody<UserSignupResponseDto>> signUp(
            @Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto response = userService.signUpUser(requestDto);
        return new ResponseEntity<>(new CommonResponseBody<>("회원가입이 완료되었습니다.", response), HttpStatus.CREATED);

    }

    /**
     * 로그인
     * @param requestDto - 로그인 요청 정보
     * @return JWT 토큰 응답
     */
    @PostMapping("/login")
    @Operation(summary = "로그인",description = "로그인 api")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> login(
            @Valid @RequestBody UserLoginRequestDto requestDto) {

        JwtAuthResponse authResponse = this.userService.login(requestDto);

        return new ResponseEntity<>(new CommonResponseBody<>("로그인을 성공했습니다.", authResponse), HttpStatus.OK);
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping
    @Operation(summary = "회원탈퇴",description = "회원탈퇴 api")
    public ResponseEntity<CommonResponseBody<Void>> deleteUser(
            @RequestBody UserDeleteRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Authentication authentication) {

        userService.deleteUser(userDetails.getUsername(),requestDto);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(FORBIDDEN_ACCESS);
        }
        new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, null);

        return new ResponseEntity<>(new CommonResponseBody<>("회원삭제가 완료되었습니다."), HttpStatus.OK);
    }

    /**
     * 프로필 수정
     * @param requestDto - 프로필 수정 요청 데이터 dto
     * @param userDetails - 요청보낸 사용자 정보 제공
     * @return - 수정된 사용자 프로필 정보 응답
     */
    @PutMapping("/profile")
    @Operation(summary = "프로필 수정",description = "프로필 수정 api")
    public ResponseEntity<CommonResponseBody<UserProfileResponseDto>> updateProfile(
            @RequestBody UserProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserProfileResponseDto responseDto = userService.updateProfile(requestDto, userDetails);

        return new ResponseEntity<>(new CommonResponseBody<>("프로필이 수정되었습니다.", responseDto), HttpStatus.OK);
    }

    /**
     * 프로필 조회
     * @param userDetails - 프로필 조회 요청 사용자의 정보 제공
     * @return - 프로필 정보 응답
     */
    @GetMapping("/profile")
    @Operation(summary = "프로필 조회",description = "프로필 조회 api")
    public ResponseEntity<CommonResponseBody<UserProfileResponseDto>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserProfileResponseDto responseDto = userService.getProfile(userDetails);

        return new ResponseEntity<>(new CommonResponseBody<>("프로필 조회를 성공하였습니다.", responseDto), HttpStatus.OK);

    }
}