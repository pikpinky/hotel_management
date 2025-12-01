package com.devpro.spring.service;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
 * Tổng cộng 26 test cases.
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

    /**
     * Test case TC-GUEST-SERVICE-010: Kiểm tra tìm kiếm guest với text không tìm thấy.
     * Expected: Trả về Page empty.
     */
    @Test
    public void testSearchGuests_NoMatch_ShouldReturnEmptyPage() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với text không match
        Page<Guest> result = guestService.searchGuests(pageable, "nonexistent");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    /**
     * Test case TC-GUEST-SERVICE-011: Kiểm tra tìm kiếm guest không pagination với text không tìm thấy.
     * Expected: Trả về List empty.
     */
    @Test
    public void testSearchGuests_WithoutPagination_NoMatch_ShouldReturnEmptyList() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Gọi phương thức với text không match
        List<Guest> result = guestService.searchGuests("nonexistent");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Test case TC-GUEST-SERVICE-013: Kiểm tra searchGuestWithCart với text search không tìm thấy.
     * Expected: Trả về null.
     */
    @Test
    public void testSearchGuestWithCart_InvalidIdCard_ShouldReturnNull() {
        // Gọi phương thức với idCard không tồn tại
        Guest result = guestService.searchGuestWithCart("999999999");

        // Kiểm tra kết quả
        assertNull(result);
    }

    /**
     * Test case TC-GUEST-SERVICE-013: Kiểm tra thêm guest với duplicate idCard.
     * Expected: Save successfully, nhưng checkExistGuest trả về >1.
     */
    @Test
    public void testAddGuestInfo_DuplicateIdCard_ShouldAllowDuplicate() {
        // Thêm guest đầu tiên
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestService.addGuestInfo(guest1);

        // Thêm guest thứ hai với cùng idCard
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "123456789", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        guestService.addGuestInfo(guest2);

        // Kiểm tra checkExistGuest trả về 2
        Integer count = guestService.checkExistGuest("123456789");
        assertEquals(Integer.valueOf(2), count);

        // DB có 2 guests
        assertEquals(2, guestRepository.count());
    }

    /**
     * Test case TC-GUEST-SERVICE-014: Kiểm tra edit guest với valid data.
     * Expected: Update successfully.
     */
    @Test
    public void testEditGuestInfo_ValidData_ShouldUpdateSuccessfully() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guest = guestRepository.save(guest);

        // Cập nhật
        guest.setGuestName("Updated Name");
        guest.setEmail("updated@example.com");

        // Gọi phương thức
        guestService.editGuestInfo(guest);

        // Kiểm tra
        Guest updated = guestRepository.findById(guest.getGuestId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Updated Name", updated.getGuestName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    /**
     * Test case TC-GUEST-SERVICE-015: Kiểm tra search guests với pagination page 1.
     * Expected: Correct pagination.
     */
    @Test
    public void testSearchGuests_PaginationPage1_ShouldReturnCorrectPage() {
        // Chuẩn bị dữ liệu test - 15 guests
        for (int i = 1; i <= 15; i++) {
            Guest guest = new Guest("Guest " + i, "1990-01-01", "12345678" + i, "P" + i, "Ha Noi", "Viet Nam", "0123456789", "guest" + i + "@example.com", "false", "false");
            guestRepository.save(guest);
        }

        Pageable pageable = PageRequest.of(1, 10); // Page 1, size 10

        // Gọi phương thức
        Page<Guest> result = guestService.searchGuests(pageable, "");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(15, result.getTotalElements());
        assertEquals(5, result.getContent().size()); // Page 1: items 11-15
        assertEquals(1, result.getNumber());
    }

    /**
     * Test case TC-GUEST-SERVICE-016: Kiểm tra search guests với text match name.
     * Expected: Return matching guests.
     */
    @Test
    public void testSearchGuests_TextMatchName_ShouldReturnMatching() {
        // Chuẩn bị dữ liệu test
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với text match name
        Page<Guest> result = guestService.searchGuests(pageable, "Nguyen");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Nguyen Van A", result.getContent().get(0).getGuestName());
    }

    /**
     * Test case TC-GUEST-SERVICE-017: Kiểm tra search guests without pagination với text match phone.
     * Expected: Return matching guests.
     */
    @Test
    public void testSearchGuests_WithoutPagination_TextMatchPhone_ShouldReturnMatching() {
        // Chuẩn bị dữ liệu test
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // Gọi phương thức với text match phone
        List<Guest> result = guestService.searchGuests("0123456789");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("0123456789", result.get(0).getPhoneNumber());
    }

    /**
     * Test case TC-GUEST-SERVICE-018: Kiểm tra checkExistGuest với empty idCard.
     * Expected: Return 0.
     */
    @Test
    public void testCheckExistGuest_EmptyIdCard_ShouldReturn0() {
        // Gọi phương thức với empty idCard
        Integer result = guestService.checkExistGuest("");

        // Kiểm tra
        assertEquals(Integer.valueOf(0), result);
    }

    /**
     * Test case TC-GUEST-SERVICE-019: Kiểm tra add guest với empty fields.
     * Expected: Save successfully.
     */
    @Test
    public void testAddGuestInfo_EmptyFields_ShouldSave() {
        // Tạo guest với empty fields
        Guest guest = new Guest("", "", "", "", "", "", "", "", "", "");

        // Gọi phương thức
        guestService.addGuestInfo(guest);

        // Kiểm tra DB có 1 guest
        assertEquals(1, guestRepository.count());
    }

    /**
     * Test case TC-GUEST-SERVICE-020: Kiểm tra search guests với large data.
     * Expected: Handle correctly.
     */
    @Test
    public void testSearchGuests_LargeData_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test - 100 guests
        for (int i = 1; i <= 100; i++) {
            Guest guest = new Guest("Guest " + i, "1990-01-01", "12345678" + i, "P" + i, "Ha Noi", "Viet Nam", "0123456789", "guest" + i + "@example.com", "false", "false");
            guestRepository.save(guest);
        }

        Pageable pageable = PageRequest.of(0, 12);

        // Gọi phương thức
        Page<Guest> result = guestService.searchGuests(pageable, "");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(100, result.getTotalElements());
        assertEquals(12, result.getContent().size());
        assertEquals(9, result.getTotalPages()); // 100/12 = 8.33, 9 pages
    }

    /**
     * Test case TC-GUEST-SERVICE-021: Kiểm tra searchGuestWithCart với valid idCard.
     * Expected: Return correct guest.
     */
    @Test
    public void testSearchGuestWithCart_ValidIdCard_ShouldReturnGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Gọi phương thức
        Guest result = guestService.searchGuestWithCart("123456789");

        // Kiểm tra
        assertNotNull(result);
        assertEquals("123456789", result.getIdCard());
        assertEquals("Nguyen Van A", result.getGuestName());
    }

    /**
     * Test case TC-GUEST-SERVICE-022: Kiểm tra find guest với valid ID.
     * Expected: Return guest.
     */
    @Test
    public void testFindGuest_ValidId_ShouldReturnGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guest = guestRepository.save(guest);

        // Gọi phương thức
        Guest result = guestService.findGuest(guest.getGuestId());

        // Kiểm tra
        assertNotNull(result);
        assertEquals(guest.getGuestId(), result.getGuestId());
    }

    /**
     * Test case TC-GUEST-SERVICE-023: Kiểm tra search guests without pagination với empty text.
     * Expected: Return all guests.
     */
    @Test
    public void testSearchGuests_WithoutPagination_EmptyText_ShouldReturnAll() {
        // Chuẩn bị dữ liệu test - 3 guests
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        Guest guest3 = new Guest("Le Van C", "1995-05-05", "111111111", "P111111", "Da Nang", "Viet Nam", "0111111111", "c@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);
        guestRepository.save(guest3);

        // Gọi phương thức với empty text
        List<Guest> result = guestService.searchGuests("");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * Test case TC-GUEST-SERVICE-024: Kiểm tra checkExistGuest với null idCard.
     * Expected: Handle gracefully, return 0.
     */
    @Test
    public void testCheckExistGuest_NullIdCard_ShouldReturn0() {
        // Gọi phương thức với null
        Integer result = guestService.checkExistGuest(null);

        // Kiểm tra
        assertEquals(Integer.valueOf(0), result);
    }

    /**
     * Test case TC-GUEST-SERVICE-025: Kiểm tra edit guest với changed isVip.
     * Expected: Update isVip successfully.
     */
    @Test
    public void testEditGuestInfo_ChangeVip_ShouldUpdateVip() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guest = guestRepository.save(guest);

        // Cập nhật isVip
        guest.setIsVip("true");

        // Gọi phương thức
        guestService.editGuestInfo(guest);

        // Kiểm tra
        Guest updated = guestRepository.findById(guest.getGuestId()).orElse(null);
        assertNotNull(updated);
        assertEquals("true", updated.getIsVip());
    }

    /**
     * Test case TC-GUEST-SERVICE-026: Kiểm tra search guests với page size 1.
     * Expected: Correct pagination.
     */
    @Test
    public void testSearchGuests_PageSize1_ShouldPaginateCorrectly() {
        // Chuẩn bị dữ liệu test - 3 guests
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        Guest guest3 = new Guest("Le Van C", "1995-05-05", "111111111", "P111111", "Da Nang", "Viet Nam", "0111111111", "c@example.com", "false", "false");
        guestRepository.save(guest1);
        guestRepository.save(guest2);
        guestRepository.save(guest3);

        Pageable pageable = PageRequest.of(0, 1); // Page 0, size 1

        // Gọi phương thức
        Page<Guest> result = guestService.searchGuests(pageable, "");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(3, result.getTotalPages());
    }
}
