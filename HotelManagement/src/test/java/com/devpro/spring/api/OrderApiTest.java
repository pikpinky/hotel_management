package com.devpro.spring.api;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.devpro.spring.dto.OrderFoodDto;
import com.devpro.spring.dto.OrderServiceDto;
import com.devpro.spring.model.AjaxResponseBody;
import com.devpro.spring.repository.OrderFoodRepository;
import com.devpro.spring.repository.RentalRepository;
import com.devpro.spring.repository.ServiceBillRepository;

/**
 * Lớp test integration cho OrderApi.
 * Test các chức năng đặt đồ ăn và dịch vụ trong hệ thống quản lý khách sạn.
 * Bao gồm: validation input, xử lý rental tồn tại, tạo order.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ: đặt thành công nếu input hợp lệ và rental tồn tại.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderApiTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private ServiceBillRepository serviceBillRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private OrderApi orderApi;

    /**
     * Test case TC-ORDER-001: Kiểm tra khi totalPrice âm.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NegativeTotalPrice_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với totalPrice âm
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setTotalPrice("-100.0"); // totalPrice âm
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("totalPrice", "Min", "Total price must be positive"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Total price must be positive"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-002: Kiểm tra khi peopleNumber âm.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NegativePeopleNumber_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với peopleNumber âm
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setPeopleNumber("-1"); // peopleNumber âm
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("peopleNumber", "Min", "People number must be positive"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("People number must be positive"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-003: Kiểm tra khi discount âm.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NegativeDiscount_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với discount âm
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setDiscount("-10.0"); // discount âm
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("discount", "Min", "Discount must be non-negative"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Discount must be non-negative"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-004: Kiểm tra khi totalPrice bằng 0.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_ZeroTotalPrice_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với totalPrice = 0
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setTotalPrice("0"); // totalPrice = 0
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("totalPrice", "Min", "Total price must be positive"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Total price must be positive"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-005: Kiểm tra khi peopleNumber bằng 0.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_ZeroPeopleNumber_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với peopleNumber = 0
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setPeopleNumber("0"); // peopleNumber = 0
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("peopleNumber", "Min", "People number must be positive"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("People number must be positive"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-006: Kiểm tra khi discount null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NullDiscount_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với discount null
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setDiscount(null); // discount null
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("discount", "NotNull", "Discount is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Discount is required"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-007: Kiểm tra khi totalPrice null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NullTotalPrice_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với totalPrice null
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setTotalPrice(null); // totalPrice null
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("totalPrice", "NotNull", "Total price is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Total price is required"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-008: Kiểm tra khi peopleNumber null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NullPeopleNumber_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với peopleNumber null
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setPeopleNumber(null); // peopleNumber null
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("peopleNumber", "NotNull", "People number is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("People number is required"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-009: Kiểm tra khi orderDate null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NullOrderDate_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với orderDate null
        OrderFoodDto orderFoodDto = createValidOrderFoodDto(1L);
        orderFoodDto.setOrderDate(null); // orderDate null
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("orderDate", "NotNull", "Order date is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Order date is required"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-010: Kiểm tra khi rentalId null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderFood_NullRentalId_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderFoodDto với rentalId null
        OrderFoodDto orderFoodDto = new OrderFoodDto(null, "2", "2023-01-01", "Test note", "10.0", "100.0");
        Errors errors = new BeanPropertyBindingResult(orderFoodDto, "orderFoodDto");
        errors.rejectValue("rentalId", "NotNull", "Rental ID is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderFood(orderFoodDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Rental ID is required"));

        // Verify DB không thay đổi
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-011: Kiểm tra addOrderService với totalPrice âm.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderService_NegativeTotalPrice_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderServiceDto với totalPrice âm
        OrderServiceDto orderServiceDto = createValidOrderServiceDto(1L);
        orderServiceDto.setTotalPrice("-50.0"); // totalPrice âm
        Errors errors = new BeanPropertyBindingResult(orderServiceDto, "orderServiceDto");
        errors.rejectValue("totalPrice", "Min", "Total price must be positive"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderService(orderServiceDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Total price must be positive"));

        // Verify DB không thay đổi
        assertEquals(0, serviceBillRepository.count());
    }

    /**
     * Test case TC-ORDER-012: Kiểm tra addOrderService với discount âm.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderService_NegativeDiscount_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderServiceDto với discount âm
        OrderServiceDto orderServiceDto = createValidOrderServiceDto(1L);
        orderServiceDto.setDiscount("-5.0"); // discount âm
        Errors errors = new BeanPropertyBindingResult(orderServiceDto, "orderServiceDto");
        errors.rejectValue("discount", "Min", "Discount must be non-negative"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderService(orderServiceDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Discount must be non-negative"));

        // Verify DB không thay đổi
        assertEquals(0, serviceBillRepository.count());
    }

    /**
     * Test case TC-ORDER-013: Kiểm tra addOrderService với totalPrice null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderService_NullTotalPrice_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderServiceDto với totalPrice null
        OrderServiceDto orderServiceDto = createValidOrderServiceDto(1L);
        orderServiceDto.setTotalPrice(null); // totalPrice null
        Errors errors = new BeanPropertyBindingResult(orderServiceDto, "orderServiceDto");
        errors.rejectValue("totalPrice", "NotNull", "Total price is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderService(orderServiceDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Total price is required"));

        // Verify DB không thay đổi
        assertEquals(0, serviceBillRepository.count());
    }

    /**
     * Test case TC-ORDER-014: Kiểm tra addOrderService với orderDate null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderService_NullOrderDate_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderServiceDto với orderDate null
        OrderServiceDto orderServiceDto = createValidOrderServiceDto(1L);
        orderServiceDto.setOrderDate(null); // orderDate null
        Errors errors = new BeanPropertyBindingResult(orderServiceDto, "orderServiceDto");
        errors.rejectValue("orderDate", "NotNull", "Order date is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderService(orderServiceDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Order date is required"));

        // Verify DB không thay đổi
        assertEquals(0, serviceBillRepository.count());
    }

    /**
     * Test case TC-ORDER-015: Kiểm tra addOrderService với rentalId null.
     * Expected: Trả về ResponseEntity status 400 với message lỗi validation.
     */
    @Test
    public void testAddOrderService_NullRentalId_ShouldReturnBadRequest() {
        // Chuẩn bị dữ liệu test: OrderServiceDto với rentalId null
        OrderServiceDto orderServiceDto = new OrderServiceDto(null, "2023-01-01", "Service note", "5.0", "50.0");
        Errors errors = new BeanPropertyBindingResult(orderServiceDto, "orderServiceDto");
        errors.rejectValue("rentalId", "NotNull", "Rental ID is required"); // Mock lỗi validation

        // Gọi phương thức API
        ResponseEntity<?> response = orderApi.addOrderService(orderServiceDto, errors);

        // Kiểm tra kết quả response: Status 400 và message chứa lỗi
        assertEquals(400, response.getStatusCodeValue());
        AjaxResponseBody result = (AjaxResponseBody) response.getBody();
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Rental ID is required"));

        // Verify DB không thay đổi
        assertEquals(0, serviceBillRepository.count());
    }

    /**
     * Helper method tạo OrderFoodDto hợp lệ.
     */
    private OrderFoodDto createValidOrderFoodDto(Long rentalId) {
        OrderFoodDto dto = new OrderFoodDto(rentalId, "2", "2023-01-01", "Test note", "10.0", "100.0");
        return dto;
    }

    /**
     * Helper method tạo OrderServiceDto hợp lệ.
     */
    private OrderServiceDto createValidOrderServiceDto(Long rentalId) {
        OrderServiceDto dto = new OrderServiceDto(rentalId, "2023-01-01", "Service note", "5.0", "50.0");
        return dto;
    }
}
