package com.devpro.spring.service;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.Guest;
import com.devpro.spring.model.OrderFood;
import com.devpro.spring.model.Payment;
import com.devpro.spring.model.Rental;
import com.devpro.spring.repository.GuestRepository;
import com.devpro.spring.repository.OrderFoodRepository;
import com.devpro.spring.repository.RentalRepository;

/**
 * Lớp test integration cho OrderFoodService.
 * Test các chức năng đặt đồ ăn trong hệ thống quản lý khách sạn.
 * Bao gồm: validation dữ liệu đầu vào theo logic nghiệp vụ thực tế, kiểm tra rental tồn tại, tạo order food.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ thực tế của khách sạn: tổng tiền phải > 0, số người phải > 0, rental phải tồn tại, ngày đặt bắt buộc.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderFoodServiceTest {

    @Autowired
    private OrderFoodService orderFoodService;

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EntityManager em;

    /**
     * Test case TC-ORDERFOOD-SERVICE-001: Kiểm tra addOrderFood với dữ liệu hợp lệ và rental tồn tại.
     * Expected: Order food được tạo thành công và lưu vào database.
     */
    @Test
    public void testAddOrderFood_ValidData_ShouldSaveSuccessfully() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ trước
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với dữ liệu hợp lệ theo logic nghiệp vụ khách sạn
        OrderFood orderFood = new OrderFood("150000", "4", "2024-12-01 10:00:00", "10000", "Gọi món sáng", rental);

        long initialCount = orderFoodRepository.count();

        // Gọi service method
        orderFoodService.addOrderFood(orderFood);

        // Kiểm tra kết quả: Order food đã được lưu vào database
        assertEquals(initialCount + 1, orderFoodRepository.count());
        
        // Verify order food details trong database
        OrderFood savedOrder = orderFoodRepository.findAll().get((int)initialCount);
        assertNotNull(savedOrder.getId());
        assertEquals("150000", savedOrder.getTotalPrice());
        assertEquals("4", savedOrder.getPeopleNumber());
        assertEquals("2024-12-01 10:00:00", savedOrder.getOrderDate());
        assertEquals("10000", savedOrder.getDiscount());
        assertEquals("Gọi món sáng", savedOrder.getNote());
        assertEquals(rental.getRentalId(), savedOrder.getRental().getRentalId());
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-002: Kiểm tra addOrderFood với rental null.
     * Expected: Lỗi vì trong khách sạn không thể đặt món mà không có booking phòng (rental).
     */
    @Test
    public void testAddOrderFood_NullRental_ShouldFail() {
        // Chuẩn bị dữ liệu test: OrderFood với rental null
        OrderFood orderFood = new OrderFood("100000", "2", "2024-12-01 12:00:00", "5000", "Test order", null);

        try {
            // Gọi service method - Expected: Exception vì không thể đặt món mà không có booking
            orderFoodService.addOrderFood(orderFood);
            fail("Expected exception when rental is null - không thể đặt món mà không có booking phòng");
        } catch (Exception e) {
            // Expected - đây là lỗi nghiệp vụ đúng
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-003: Kiểm tra addOrderFood với totalPrice âm.
     * Expected: Lỗi vì tổng tiền âm không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_NegativeTotalPrice_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với totalPrice âm - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("-50000", "2", "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì tổng tiền âm không hợp lệ
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order với tổng tiền âm - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-004: Kiểm tra addOrderFood với totalPrice bằng 0.
     * Expected: Lỗi vì tổng tiền 0 không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_ZeroTotalPrice_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với totalPrice = 0 - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("0", "2", "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì tổng tiền 0 không hợp lệ
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order với tổng tiền 0 - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-005: Kiểm tra addOrderFood với peopleNumber âm.
     * Expected: Lỗi vì số người âm không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_NegativePeopleNumber_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với peopleNumber âm - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("100000", "-2", "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì số người âm không hợp lệ
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order với số người âm - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-006: Kiểm tra addOrderFood với peopleNumber bằng 0.
     * Expected: Lỗi vì phải có ít nhất 1 người để đặt món trong khách sạn.
     */
    @Test
    public void testAddOrderFood_ZeroPeopleNumber_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với peopleNumber = 0 - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("100000", "0", "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì phải có ít nhất 1 người đặt món
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order với 0 người - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-007: Kiểm tra addOrderFood với discount âm.
     * Expected: Lỗi vì giảm giá âm không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_NegativeDiscount_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với discount âm - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("100000", "2", "2024-12-01", "-10000", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì giảm giá âm không hợp lệ
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order với giảm giá âm - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-008: Kiểm tra addOrderFood với orderDate null.
     * Expected: Lỗi vì ngày đặt món bắt buộc trong hệ thống khách sạn.
     */
    @Test
    public void testAddOrderFood_NullOrderDate_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với orderDate null - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("100000", "2", null, "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì ngày đặt món là bắt buộc
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order mà không có ngày đặt - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-009: Kiểm tra addOrderFood với totalPrice null.
     * Expected: Lỗi vì tổng tiền null không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_NullTotalPrice_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với totalPrice null - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood(null, "2", "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì tổng tiền là bắt buộc
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order mà không có tổng tiền - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-010: Kiểm tra addOrderFood với peopleNumber null.
     * Expected: Lỗi vì số người null không hợp lệ trong nghiệp vụ khách sạn.
     */
    @Test
    public void testAddOrderFood_NullPeopleNumber_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với peopleNumber null - vi phạm logic nghiệp vụ
        OrderFood orderFood = new OrderFood("100000", null, "2024-12-01", "0", "Test", rental);

        long initialCount = orderFoodRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì số người là bắt buộc
            orderFoodService.addOrderFood(orderFood);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (orderFoodRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu order mà không có số người - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-011: Kiểm tra addOrderFood với discount null.
     * Expected: Thành công vì giảm giá có thể không có (mặc định 0).
     */
    @Test
    public void testAddOrderFood_NullDiscount_ShouldSaveSuccessfully() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với discount null - hợp lệ vì có thể không giảm giá
        OrderFood orderFood = new OrderFood("100000", "2", "2024-12-01", null, "Không giảm giá", rental);

        long initialCount = orderFoodRepository.count();

        // Gọi service method
        orderFoodService.addOrderFood(orderFood);

        // Kiểm tra kết quả: Order food được lưu thành công
        assertEquals(initialCount + 1, orderFoodRepository.count());
        
        OrderFood savedOrder = orderFoodRepository.findAll().get((int)initialCount);
        assertEquals("100000", savedOrder.getTotalPrice());
        assertEquals("2", savedOrder.getPeopleNumber());
        assertEquals(null, savedOrder.getDiscount()); // Discount có thể null
    }

    /**
     * Test case TC-ORDERFOOD-SERVICE-012: Kiểm tra addOrderFood với note null.
     * Expected: Thành công vì ghi chú có thể không có.
     */
    @Test
    public void testAddOrderFood_NullNote_ShouldSaveSuccessfully() {
        // Chuẩn bị dữ liệu test: Tạo rental hợp lệ
        Rental rental = createValidRental();
        rentalRepository.save(rental);
        em.flush();

        // Tạo OrderFood với note null - hợp lệ vì ghi chú không bắt buộc
        OrderFood orderFood = new OrderFood("100000", "2", "2024-12-01", "0", null, rental);

        long initialCount = orderFoodRepository.count();

        // Gọi service method
        orderFoodService.addOrderFood(orderFood);

        // Kiểm tra kết quả: Order food được lưu thành công
        assertEquals(initialCount + 1, orderFoodRepository.count());
        
        OrderFood savedOrder = orderFoodRepository.findAll().get((int)initialCount);
        assertEquals("100000", savedOrder.getTotalPrice());
        assertEquals("2", savedOrder.getPeopleNumber());
        assertEquals(null, savedOrder.getNote()); // Note có thể null
    }

    /**
     * Helper method tạo Rental hợp lệ cho test.
     */
    private Rental createValidRental() {
        // Tạo Guest với đúng tên method theo model
        Guest guest = new Guest();
        guest.setGuestName("Nguyen Van A");
        guest.setPhoneNumber("0123456789"); // Đúng tên method
        guest.setEmail("test@email.com");   // Đúng tên method
        guest.setAddress("123 Test Street"); // Đúng tên method
        guest.setIdCard("123456789");        // Đúng tên method
        guestRepository.save(guest);

        // Tạo Payment trực tiếp bằng EntityManager vì không có setters
        Payment payment = new Payment();
        em.persist(payment); // Persist payment trước để có ID
        em.flush();

        // Tạo Rental
        Rental rental = new Rental();
        rental.setDiscount("0");
        rental.setPaid("false");
        rental.setNote("Test rental");
        rental.setGuest(guest);
        rental.setPayment(payment);

        return rental;
    }
}
