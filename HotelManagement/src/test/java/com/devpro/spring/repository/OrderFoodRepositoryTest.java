package com.devpro.spring.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

/**
 * Lớp test integration cho OrderFoodRepository.
 * Test các chức năng repository cho OrderFood trong hệ thống quản lý khách sạn.
 * Bao gồm: save, findById, findAll, count, delete.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ: lưu và truy vấn OrderFood hợp lệ.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderFoodRepositoryTest {

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Test case TC-ORDER-FOOD-REPO-001: Kiểm tra count ban đầu.
     * Expected: Count = 0.
     */
    @Test
    public void testCount_Initially_ShouldReturnZero() {
        // Kiểm tra count ban đầu
        long count = orderFoodRepository.count();
        assertEquals(0, count);
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-002: Kiểm tra save OrderFood hợp lệ.
     * Expected: Save thành công, count tăng.
     */
    @Test
    public void testSave_ValidOrderFood_ShouldSaveSuccessfully() {
        // Tạo data hợp lệ
        Rental rental = createValidRental();
        OrderFood orderFood = new OrderFood("100.0", "2", "2023-01-01", "10.0", "Test note", rental);

        // Save
        OrderFood saved = orderFoodRepository.save(orderFood);

        // Kiểm tra
        assertNotNull(saved.getId());
        assertEquals("100.0", saved.getTotalPrice());
        assertEquals(1, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-003: Kiểm tra findById sau save.
     * Expected: Tìm thấy OrderFood.
     */
    @Test
    public void testFindById_AfterSave_ShouldReturnOrderFood() {
        // Tạo và save
        Rental rental = createValidRental();
        OrderFood orderFood = new OrderFood("150.0", "3", "2023-01-02", "5.0", "Another note", rental);
        OrderFood saved = orderFoodRepository.save(orderFood);

        // Find by id
        OrderFood found = orderFoodRepository.findById(saved.getId()).orElse(null);

        // Kiểm tra
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("150.0", found.getTotalPrice());
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-004: Kiểm tra findAll sau save nhiều.
     * Expected: Trả về list với size đúng.
     */
    @Test
    public void testFindAll_AfterSaveMultiple_ShouldReturnList() {
        // Tạo và save 2 OrderFood
        Rental rental1 = createValidRental();
        Rental rental2 = createValidRental();
        OrderFood orderFood1 = new OrderFood("200.0", "4", "2023-01-03", "20.0", "Note 1", rental1);
        OrderFood orderFood2 = new OrderFood("250.0", "5", "2023-01-04", "25.0", "Note 2", rental2);
        orderFoodRepository.save(orderFood1);
        orderFoodRepository.save(orderFood2);

        // Find all
        List<OrderFood> list = orderFoodRepository.findAll();

        // Kiểm tra
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(of -> "200.0".equals(of.getTotalPrice())));
        assertTrue(list.stream().anyMatch(of -> "250.0".equals(of.getTotalPrice())));
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-005: Kiểm tra delete.
     * Expected: Delete thành công, count giảm.
     */
    @Test
    public void testDelete_OrderFood_ShouldDeleteSuccessfully() {
        // Tạo và save
        Rental rental = createValidRental();
        OrderFood orderFood = new OrderFood("300.0", "6", "2023-01-05", "30.0", "Delete note", rental);
        OrderFood saved = orderFoodRepository.save(orderFood);
        assertEquals(1, orderFoodRepository.count());

        // Delete
        orderFoodRepository.delete(saved);

        // Kiểm tra
        assertEquals(0, orderFoodRepository.count());
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-006: Kiểm tra findById với ID không tồn tại.
     * Expected: Trả về Optional.empty().
     */
    @Test
    public void testFindById_NonExistentId_ShouldReturnEmpty() {
        // Find by non-existent id
        OrderFood found = orderFoodRepository.findById(999L).orElse(null);

        // Kiểm tra
        assertEquals(null, found);
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-007: Kiểm tra findAll khi empty.
     * Expected: Trả về list rỗng.
     */
    @Test
    public void testFindAll_WhenEmpty_ShouldReturnEmptyList() {
        // Find all
        List<OrderFood> list = orderFoodRepository.findAll();

        // Kiểm tra
        assertEquals(0, list.size());
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-008: Kiểm tra save với data biên (totalPrice lớn).
     * Expected: Save thành công.
     */
    @Test
    public void testSave_OrderFood_WithLargeTotalPrice_ShouldSaveSuccessfully() {
        // Tạo data với totalPrice lớn
        Rental rental = createValidRental();
        OrderFood orderFood = new OrderFood("999999.99", "10", "2023-01-06", "100.0", "Large amount note", rental);

        // Save
        OrderFood saved = orderFoodRepository.save(orderFood);

        // Kiểm tra
        assertNotNull(saved.getId());
        assertEquals("999999.99", saved.getTotalPrice());
    }

    /**
     * Test case TC-ORDER-FOOD-REPO-009: Kiểm tra save với note rỗng.
     * Expected: Save thành công.
     */
    @Test
    public void testSave_OrderFood_WithEmptyNote_ShouldSaveSuccessfully() {
        // Tạo data với note rỗng
        Rental rental = createValidRental();
        OrderFood orderFood = new OrderFood("50.0", "1", "2023-01-07", "0.0", "", rental);

        // Save
        OrderFood saved = orderFoodRepository.save(orderFood);

        // Kiểm tra
        assertNotNull(saved.getId());
        assertEquals("", saved.getNote());
    }

    /**
     * Helper method tạo Rental hợp lệ.
     */
    private Rental createValidRental() {
        Guest guest = new Guest();
        guest.setGuestName("Test Guest");
        guest.setIdCard("123456789");
        guest.setPhoneNumber("0123456789");
        guest = guestRepository.save(guest); // Save guest first

        Payment payment = new Payment();
        entityManager.persist(payment); // Save payment

        Rental rental = new Rental();
        rental.setGuest(guest);
        rental.setPayment(payment);
        rental.setCheckInDate(new Date());
        rental.setCheckOutDate(new Date());
        rental.setDiscount("0");
        rental.setPaid("false");
        rental.setNote("Test rental");

        return rental;
    }
}