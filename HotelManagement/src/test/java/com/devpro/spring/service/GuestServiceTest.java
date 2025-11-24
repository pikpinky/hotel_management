package com.devpro.spring.service;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.Guest;
import com.devpro.spring.repository.GuestRepository;

/**
 * Lớp test integration cho GuestServiceImpl.
 * Test các chức năng quản lý khách hàng: thêm, tìm kiếm, cập nhật, kiểm tra tồn tại.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GuestServiceTest {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestService guestService;

    /**
     * Test case TC-GUEST-SERVICE-001: Kiểm tra tìm guest theo ID thành công.
     * Expected: Trả về guest đúng với ID được truyền.
     */
    @Test
    public void testFindGuest_ShouldReturnGuestById() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guest = guestRepository.save(guest);
        Long guestId = guest.getGuestId();

        // Gọi phương thức
        Guest result = guestService.findGuest(guestId);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getGuestName());
    }

    /**
     * Test case TC-GUEST-SERVICE-002: Kiểm tra thêm guest mới thành công.
     * Expected: Gọi save trên repository với guest đúng.
     */
    @Test
    public void testAddGuestInfo_ShouldSaveNewGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Gọi phương thức
        guestService.addGuestInfo(guest);

        // Kiểm tra kết quả - guest được lưu vào DB
        Guest savedGuest = guestRepository.findAll().get(0);
        assertNotNull(savedGuest);
        assertEquals("Nguyen Van A", savedGuest.getGuestName());
        assertEquals("123456789", savedGuest.getIdCard());
    }

    /**
     * Test case TC-GUEST-SERVICE-003: Kiểm tra cập nhật guest thành công.
     * Expected: Gọi save trên repository với guest đã cập nhật.
     */
    @Test
    public void testEditGuestInfo_ShouldUpdateGuest() {
        // Chuẩn bị dữ liệu test - save guest trước
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guest = guestRepository.save(guest);

        // Cập nhật thông tin
        guest.setGuestName("Nguyen Van B");
        guest.setPhoneNumber("0987654321");

        // Gọi phương thức
        guestService.editGuestInfo(guest);

        // Kiểm tra kết quả - guest được cập nhật trong DB
        Guest updatedGuest = guestRepository.findById(guest.getGuestId()).orElse(null);
        assertNotNull(updatedGuest);
        assertEquals("Nguyen Van B", updatedGuest.getGuestName());
        assertEquals("0987654321", updatedGuest.getPhoneNumber());
    }

    /**
     * Test case TC-GUEST-SERVICE-004: Kiểm tra tìm kiếm guest với pagination thành công.
     * Expected: Trả về Page<Guest> với dữ liệu đúng.
     */
    @Test
    public void testSearchGuests_WithPagination_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test - save guests vào DB
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức tìm kiếm với text trống (sẽ tìm tất cả)
        Page<Guest> result = guestService.searchGuests(pageable, "");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 2); // Có ít nhất 2 guests
        assertTrue(result.getContent().size() >= 2);
    }

    /**
     * Test case TC-GUEST-SERVICE-005: Kiểm tra tìm kiếm guest không có pagination thành công.
     * Expected: Trả về List<Guest> với dữ liệu đúng.
     */
    @Test
    public void testSearchGuests_WithoutPagination_ShouldReturnList() {
        // Chuẩn bị dữ liệu test - save guest vào DB
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Gọi phương thức tìm kiếm với text trống
        List<Guest> result = guestService.searchGuests("");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.size() >= 1); // Có ít nhất 1 guest
        assertEquals("Nguyen Van A", result.get(0).getGuestName());
    }

    /**
     * Test case TC-GUEST-SERVICE-006: Kiểm tra tìm kiếm guest với cart (đã check-in) thành công.
     * Expected: Trả về guest đúng với idCard.
     */
    @Test
    public void testSearchGuestWithCart_ShouldReturnGuestByIdCard() {
        // Chuẩn bị dữ liệu test - save guest vào DB
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Gọi phương thức
        Guest result = guestService.searchGuestWithCart("123456789");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("123456789", result.getIdCard());
    }

    /**
     * Test case TC-GUEST-SERVICE-007: Kiểm tra kiểm tra tồn tại guest - không tồn tại.
     * Expected: Trả về 0 khi guest không tồn tại.
     */
    @Test
    public void testCheckExistGuest_NotExist_ShouldReturn0() {
        // Gọi phương thức với idCard không tồn tại
        Integer result = guestService.checkExistGuest("999999999");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(0), result);
    }

    /**
     * Test case TC-GUEST-SERVICE-008: Kiểm tra kiểm tra tồn tại guest - tồn tại 1 bản ghi.
     * Expected: Trả về 1 khi guest tồn tại 1 bản ghi.
     */
    @Test
    public void testCheckExistGuest_ExistOne_ShouldReturn1() {
        // Chuẩn bị dữ liệu test - save guest vào DB
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Gọi phương thức
        Integer result = guestService.checkExistGuest("123456789");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(1), result);
    }

    /**
     * Test case TC-GUEST-SERVICE-009: Kiểm tra kiểm tra tồn tại guest - duplicate.
     * Expected: Trả về 2 khi có duplicate trong DB.
     */
    @Test
    public void testCheckExistGuest_Duplicate_ShouldReturn2() {
        // Chuẩn bị dữ liệu test - save 2 guests với cùng idCard
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "123456789", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // Gọi phương thức
        Integer result = guestService.checkExistGuest("123456789");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(2), result);
    }
}
