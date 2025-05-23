package com.doubleowner.revibe.domain.coupon.controller;

import com.doubleowner.revibe.domain.coupon.dto.request.CouponRequestDto;
import com.doubleowner.revibe.domain.coupon.dto.response.CouponResponseDto;
import com.doubleowner.revibe.domain.coupon.service.CouponService;
import com.doubleowner.revibe.global.common.dto.CommonResponseBody;
import com.doubleowner.revibe.global.config.auth.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "쿠폰 관련 API")
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 생성
    @PostMapping
    @Operation(summary = "쿠폰 생성 API",description = "관리자는 쿠폰을 생성할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "쿠폰을 생성 성공하면 201 CREATED")
    })
    public ResponseEntity<CommonResponseBody<CouponResponseDto>> createCoupon(
            @RequestBody CouponRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CouponResponseDto couponResponseDto = couponService.createCoupon(userDetails.getUser(), dto);

        return new ResponseEntity<>(new CommonResponseBody<>("쿠폰 등록이 완료되었습니다.", couponResponseDto), HttpStatus.CREATED);

    }

    // 쿠폰 조회
    @GetMapping
    @Operation(summary = "쿠폰 조회 API",description = "쿠폰을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "쿠폰을 조회 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<List<CouponResponseDto>>> findCoupon(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size
    ) {

        List<CouponResponseDto> couponResponseDto = couponService.findCoupons(userDetails.getUser(), page, size);

        return new ResponseEntity<>(new CommonResponseBody<>("쿠폰 조회를 성공하였습니다.", couponResponseDto), HttpStatus.OK);
    }

    // 쿠폰 수정
    @PatchMapping("/{id}")
    @Operation(summary = "쿠폰 수정 API",description = "관리자는 쿠폰 정보를  수정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "쿠폰을 수정 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<CouponResponseDto>> updateCoupon(
            @RequestBody CouponRequestDto dto, @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CouponResponseDto couponResponseDto = couponService.updateCoupon(userDetails.getUser(), id, dto);

        return new ResponseEntity<>(new CommonResponseBody<>("쿠폰 정보를 수정하였습니다.", couponResponseDto), HttpStatus.OK);
    }

    // 쿠폰 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "쿠폰 삭제 API",description = "관리자는 쿠폰을 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "쿠폰을 삭제 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<Void>> deleteCoupon(
            @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        couponService.deleteCoupon(userDetails.getUser(), id);

        return new ResponseEntity<>(new CommonResponseBody<>("쿠폰 삭제가 완료되었습니다."), HttpStatus.OK);
    }
}
