package com.deliveryapp.catchabite.controller;

/**
 * OrderController: 사용자 주문 관리 HTTP 엔드포인트
 * * Description: 사용자가 장바구니 상품을 주문하고, 주문 내역을 확인하거나 취소합니다.
 * * 주요 기능:
 * 1. 주문 생성 (createOrder) - 장바구니 상품 주문 접수 [POST, Return: StoreOrderDTO]
 * 2. 주문 목록 조회 (getOrders) - 과거 주문 내역 리스트 조회 [GET, Return: List<StoreOrderDTO>]
 * 3. 주문 상세 조회 (getOrderDetails) - 특정 주문의 상세 정보 조회 [GET, Return: StoreOrderDTO]
 * 4. 주문 취소 (cancelOrder) - 접수 대기 중인 주문 취소 [POST, Return: Void]
 */

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.service.UserStoreOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appuser/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserStoreOrderService userStoreOrderService;

    /**
     * 장바구니의 상품으로 새로운 주문을 생성합니다.
     * POST /api/v1/appuser/orders
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StoreOrderDTO>> createOrder(@RequestBody StoreOrderDTO dto) {
        StoreOrderDTO createdOrder = userStoreOrderService.createStoreOrder(dto);
        return ResponseEntity.ok(ApiResponse.ok(createdOrder));
    }

    /**
     * 사용자의 주문 내역 목록을 조회합니다.
     * GET /api/v1/appuser/orders
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> getOrder(@PathVariable Long orderId) {
        StoreOrderDTO order = userStoreOrderService.getStoreOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    /**
     * 특정 주문의 상세 정보를 조회합니다.
     * GET /api/v1/appuser/orders/{orderId}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreOrderDTO>>> getAllOrders() {
        List<StoreOrderDTO> orders = userStoreOrderService.getAllStoreOrders();
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    /**
     * 주문을 취소합니다 (접수 전 상태일 경우).
     * POST /api/v1/appuser/orders/{orderId}/cancel
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> updateOrder(@PathVariable Long orderId, @RequestBody StoreOrderDTO dto) {
        StoreOrderDTO updatedOrder = userStoreOrderService.updateStoreOrder(orderId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedOrder));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        boolean deleted = userStoreOrderService.deleteStoreOrder(orderId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.okMessage("Order cancelled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("CANCEL_FAILED", "Failed to cancel order"));
        }
    }
}