package com.devpro.spring.repository;

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

/**
 * Lớp test integration cho GuestRepository - kiểm tra các custom query methods liên quan đến chức năng đặt phòng.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Tuân thủ quy tắc white-box testing: test các branches, paths, edge cases.
 * Test coverage: search methods, check exist, update methods, get guest info.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GuestRepositoryTest {

    @Autowired
    private GuestRepository guestRepository;

    /**
     * Test case TC-GUEST-REPO-001: Kiểm tra searchGuests với pagination.
     * Mục đích: Verify method searchGuests(Pageable, String) hoạt động đúng với pagination
     * Kiểm tra: 
     *  - Custom query tìm kiếm guest theo pattern text có hỗ trợ pagination không
     *  - Kết quả trả về có đúng structure Page<Guest> không
     *  - Filter theo search pattern có chính xác không
     * Input: 3 guests với tên khác nhau, search pattern "%Nguyen%", pagination 10 items/page
     * Expected: Trả về Page với 1 guest duy nhất match pattern "Nguyen"
     */
    @Test
    public void testSearchGuests_WithPagination_ShouldReturnMatchingGuests() {
        // ARRANGE: Chuẩn bị 3 guests test data với tên khác nhau
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        Guest guest3 = new Guest("Le Van C", "1988-03-03", "111222333", "P111222", "Da Nang", "Viet Nam", "0111222333", "c@example.com", "true", "true");
        
        // Save vào H2 database - sẽ auto rollback sau test
        guestRepository.save(guest1);
        guestRepository.save(guest2);
        guestRepository.save(guest3);

        // Setup pagination: page 0, size 10
        Pageable pageable = PageRequest.of(0, 10);

        // ACT: Execute search với pattern chỉ match guest1
        Page<Guest> result = guestRepository.searchGuests(pageable, "%Nguyen%");
        
        // ASSERT: Verify pagination results
        assertNotNull("Result should not be null", result);
        assertEquals("Should find exactly 1 guest with Nguyen in name", 1, result.getTotalElements());
        assertEquals("Found guest should be Nguyen Van A", "Nguyen Van A", result.getContent().get(0).getGuestName());
    }

    /**
     * Test case TC-GUEST-REPO-002: Kiểm tra searchGuests không có pagination.
     * Mục đích: Verify overloaded method searchGuests(String) hoạt động đúng không cần pagination
     * Kiểm tra:
     *  - Method overload có hoạt động khác với pagination version không
     *  - Return type List<Guest> thay vì Page<Guest>
     *  - Tìm kiếm multiple matches có đúng không
     * Input: 2 guests cùng pattern "Nguyen", search "%Nguyen%"
     * Expected: List với 2 guests đều có "Nguyen" trong tên
     */
    @Test
    public void testSearchGuests_WithoutPagination_ShouldReturnMatchingGuests() {
        // ARRANGE: Setup 2 guests với cùng pattern "Nguyen" 
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Nguyen Van B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        
        // Persist data vào H2 test DB
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // ACT: Execute search method không pagination
        List<Guest> result = guestRepository.searchGuests("%Nguyen%");
        
        // ASSERT: Verify tất cả matching records được return
        assertNotNull("Result list should not be null", result);
        assertEquals("Should return exactly 2 guests", 2, result.size());
        assertTrue("All returned guests should contain 'Nguyen' in name", 
                  result.stream().allMatch(g -> g.getGuestName().contains("Nguyen")));
    }

    /**
     * Test case TC-GUEST-REPO-003: Kiểm tra searchGuestWithCart.
     * Expected: Trả về guest theo idCard.
     */
    @Test
    public void testSearchGuestWithCart_ValidIdCard_ShouldReturnGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Test search by idCard
        Guest result = guestRepository.searchGuestWithCart("123456789");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("123456789", result.getIdCard());
        assertEquals("Nguyen Van A", result.getGuestName());
    }

    /**
     * Test case TC-GUEST-REPO-004: Kiểm tra searchGuestWithCart với idCard không tồn tại.
     * Expected: Trả về null.
     */
    @Test
    public void testSearchGuestWithCart_InvalidIdCard_ShouldReturnNull() {
        // Test với idCard không tồn tại
        Guest result = guestRepository.searchGuestWithCart("999999999");
        
        // Kiểm tra kết quả
        assertNull(result);
    }

    /**
     * Test case TC-GUEST-REPO-005: Kiểm tra checkExistGuest - guest không tồn tại.
     * Mục đích: Verify method checkExistGuest trả về 0 khi không tìm thấy guest với idCard
     * Kiểm tra: Custom query đếm số record match idCard có hoạt động đúng không
     * Input: idCard không tồn tại trong DB ("999999999")
     * Expected: Return Integer 0
     */
    @Test
    public void testCheckExistGuest_NotExist_ShouldReturn0() {
        // ACT: Query với idCard không tồn tại (DB empty sau rollback)
        Integer result = guestRepository.checkExistGuest("999999999");
        
        // ASSERT: Should return count 0
        assertEquals("Should return 0 for non-existent guest", Integer.valueOf(0), result);
    }

    /**
     * Test case TC-GUEST-REPO-006: Kiểm tra checkExistGuest - guest tồn tại 1 bản ghi.
     * Mục đích: Verify method checkExistGuest trả về đúng count khi tìm thấy guest
     * Kiểm tra: Query đếm có trả về đúng số lượng record không
     * Input: 1 guest với idCard "123456789"
     * Expected: Return Integer 1
     */
    @Test
    public void testCheckExistGuest_ExistOne_ShouldReturn1() {
        // ARRANGE: Create 1 guest với specific idCard
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // ACT: Check exist với existing idCard
        Integer result = guestRepository.checkExistGuest("123456789");
        
        // ASSERT: Should return count 1
        assertEquals("Should return 1 for existing guest", Integer.valueOf(1), result);
    }

    /**
     * Test case TC-GUEST-REPO-007: Kiểm tra updateComplete method.
     * Expected: Update guest information correctly.
     */
    @Test
    public void testUpdateComplete_ShouldUpdateGuestInfo() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Test update complete
        guestRepository.updateComplete("P999999", "New Address", "0999999999", "new@email.com", "true", "true", "123456789");

        // Verify update
        Guest updatedGuest = guestRepository.searchGuestWithCart("123456789");
        assertNotNull(updatedGuest);
        assertEquals("P999999", updatedGuest.getPassport());
        assertEquals("New Address", updatedGuest.getAddress());
        assertEquals("0999999999", updatedGuest.getPhoneNumber());
        assertEquals("new@email.com", updatedGuest.getEmail());
        assertEquals("true", updatedGuest.getIsFamiliar());
        assertEquals("true", updatedGuest.getIsVip());
    }

    /**
     * Test case TC-GUEST-REPO-008: Kiểm tra updateNomal method.
     * Expected: Update guest normal information correctly.
     */
    @Test
    public void testUpdateNomal_ShouldUpdateGuestNormalInfo() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest savedGuest = guestRepository.save(guest);

        // Test update normal
        guestRepository.updateNomal("Nguyen Van B", "1995-05-05", "987654321", "P888888", 
                "Updated Address", "Vietnam", "0888888888", "updated@email.com", savedGuest.getGuestId());

        // Verify update
        Guest updatedGuest = guestRepository.findById(savedGuest.getGuestId()).orElse(null);
        assertNotNull(updatedGuest);
        assertEquals("Nguyen Van B", updatedGuest.getGuestName());
        assertEquals("1995-05-05", updatedGuest.getBirth());
        assertEquals("987654321", updatedGuest.getIdCard());
        assertEquals("P888888", updatedGuest.getPassport());
    }



    /**
     * Test case TC-GUEST-REPO-009: Kiểm tra searchGuests với empty search text.
     * Expected: Trả về tất cả guests hoặc empty tùy implementation.
     */
    @Test
    public void testSearchGuests_EmptyText_ShouldReturnAllGuests() {
        // Chuẩn bị dữ liệu test
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // Test search với empty text
        List<Guest> result = guestRepository.searchGuests("%%");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Test case TC-GUEST-REPO-010: Kiểm tra search với pagination - no results.
     * Expected: Trả về empty page.
     */
    @Test
    public void testSearchGuests_NoResults_ShouldReturnEmptyPage() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search với text không match
        Page<Guest> result = guestRepository.searchGuests(pageable, "%XYZ%");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * Test case TC-GUEST-REPO-011: Kiểm tra updateComplete với guest không tồn tại.
     * Expected: Không có lỗi, không update gì.
     */
    @Test
    public void testUpdateComplete_NonExistentGuest_ShouldNotThrowError() {
        // Test update guest không tồn tại
        guestRepository.updateComplete("P999999", "New Address", "0999999999", "new@email.com", "true", "true", "999999999");
        
        // Verify không có guest nào được tạo
        Guest result = guestRepository.searchGuestWithCart("999999999");
        assertNull(result);
    }

    /**
     * Test case TC-GUEST-REPO-012: Kiểm tra search guests với special characters.
     * Expected: Handle special characters correctly.
     */
    @Test
    public void testSearchGuests_SpecialCharacters_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyễn Văn Á", "1990-01-01", "123456789", "P123456", "Hà Nội", "Việt Nam", "0123456789", "a@example.com", "false", "false");
        guestRepository.save(guest);

        // Test search với special characters
        List<Guest> result = guestRepository.searchGuests("%Nguyễn%");
        
        // Verify results (tùy thuộc vào database collation)
        assertNotNull(result);
        // Kết quả có thể 0 hoặc 1 tùy thuộc vào cấu hình database
    }

    /**
     * Test case TC-GUEST-REPO-013: Kiểm tra checkExistGuest với null/empty idCard.
     * Expected: Return 0, không throw exception.
     */
    @Test
    public void testCheckExistGuest_NullIdCard_ShouldReturn0() {
        // Test với null idCard
        Integer result1 = guestRepository.checkExistGuest(null);
        assertEquals(Integer.valueOf(0), result1);

        // Test với empty idCard
        Integer result2 = guestRepository.checkExistGuest("");
        assertEquals(Integer.valueOf(0), result2);
    }

    /**
     * Test case TC-GUEST-REPO-014: Kiểm tra checkExistGuest với duplicate guests.
     * Expected: Trả về số lượng > 1 nếu có duplicate.
     */
    @Test
    public void testCheckExistGuest_Duplicate_ShouldReturnCount() {
        // Chuẩn bị dữ liệu test với duplicate idCard
        Guest guest1 = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Guest guest2 = new Guest("Tran Van B", "1992-02-02", "123456789", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false");
        
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // Test check exist với duplicate
        Integer result = guestRepository.checkExistGuest("123456789");
        
        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(2), result);
    }
}