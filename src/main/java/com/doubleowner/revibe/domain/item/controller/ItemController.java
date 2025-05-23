package com.doubleowner.revibe.domain.item.controller;

import com.doubleowner.revibe.domain.item.dto.request.ItemRequestDto;
import com.doubleowner.revibe.domain.item.dto.request.ItemUpdateRequestDto;
import com.doubleowner.revibe.domain.item.dto.response.ItemResponseDto;
import com.doubleowner.revibe.domain.item.service.ItemService;
import com.doubleowner.revibe.domain.review.dto.ReviewResponseDto;
import com.doubleowner.revibe.domain.review.service.ReviewService;
import com.doubleowner.revibe.domain.user.entity.User;
import com.doubleowner.revibe.global.common.dto.CommonResponseBody;
import com.doubleowner.revibe.global.config.auth.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "상품 관련 API")
public class ItemController {

    private final ItemService itemService;
    private final ReviewService reviewService;

    // 상품 등록
    @PostMapping
    @Operation(summary = "상품 등록 API",description = "상품을 등록할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "상품을 등록 성공하면 201 CREATED")
    })
    public ResponseEntity<CommonResponseBody<ItemResponseDto>> createItem(
            @Valid @RequestBody ItemRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User loginUser = userDetails.getUser();
        ItemResponseDto responseDto = itemService.createItem(loginUser, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseBody<>("상품을 등록했습니다", responseDto));
    }

    // 상품 수정
    @PatchMapping("/{itemId}")
    @Operation(summary = "상품 수정 API",description = "상품을 수정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "상품에 대한 정보 수정 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<ItemResponseDto>> updateItem(
            @Valid @ModelAttribute ItemUpdateRequestDto requestDto,
            @PathVariable Long itemId
    ) {
        ItemResponseDto responseDto = itemService.modifyItem(itemId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body((new CommonResponseBody<>("상품이 수정 되었습니다.", responseDto)));
    }

    // 상품 전체 조회
    @GetMapping
    @Operation(summary = "상품 조회 API",description = "상품을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "상품 조회 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<List<ItemResponseDto>>> getItems(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "brand", required = false) String brand
    ) {
        List<ItemResponseDto> responseDtos = itemService.getAllItems(page, size, keyword, brand);
        return ResponseEntity.status(HttpStatus.OK).body((new CommonResponseBody<>("상품들을 조회했습니다.", responseDtos)));
    }

    // 상품 단건 조회
    @GetMapping("/{itemId}")
    @Operation(summary = "상품 상세 정보 조회 API",description = "상품의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "상품 조회 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<ItemResponseDto>> getItemDetail(@PathVariable Long itemId) {
        ItemResponseDto responseDto = itemService.getItem(itemId);
        return ResponseEntity.status(HttpStatus.OK).body((new CommonResponseBody<>("상품을 조회했습니다.", responseDto)));
    }

    @GetMapping("/{itemId}/reviews")
    @Operation(summary = "상품에 대한 리뷰 조회 API",description = "상품의 리뷰를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "리뷰 조회 성공하면 200 OK")
    })
    public ResponseEntity<CommonResponseBody<List<ReviewResponseDto>>> getItemReviews(
            @PathVariable Long itemId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size
    ) {
        List<ReviewResponseDto> responseDto = reviewService.findItemReviews(itemId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body((new CommonResponseBody<>("리뷰를 조회 했습니다.", responseDto)));

    }

}