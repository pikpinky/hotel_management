package com.devpro.spring.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devpro.spring.model.Guest;
import com.devpro.spring.repository.GuestRepository;

/**
 * Lớp test unit cho GuestServiceImpl.
 * Test các chức năng quản lý khách hàng: thêm, tìm kiếm, cập nhật, kiểm tra tồn tại.
 * Sử dụng Mockito để mock GuestRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private GuestServiceImpl guestService;

    /**
     * Test case TC-GUEST-SERVICE-001: Kiểm tra tìm guest theo ID thành công.
     * Expected: Trả về guest đúng với ID được truyền.
     */
    @Test
    public void testFindGuest_ShouldReturnGuestById() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Mock repository
        when(guestRepository.getOne(1L)).thenReturn(guest);

        // Gọi phương thức
        Guest result = guestService.findGuest(1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getGuestName());
        verify(guestRepository).getOne(1L);
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

        // Verify gọi save
        verify(guestRepository).save(guest);
    }

    /**
     * Test case TC-GUEST-SERVICE-003: Kiểm tra cập nhật guest thành công.
     * Expected: Gọi save trên repository với guest đã cập nhật.
     */
    @Test
    public void testEditGuestInfo_ShouldUpdateGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Gọi phương thức
        guestService.editGuestInfo(guest);

        // Verify gọi save
        verify(guestRepository).save(guest);
    }

    /**
     * Test case TC-GUEST-SERVICE-004: Kiểm tra tìm kiếm guest với pagination thành công.
     * Expected: Trả về Page<Guest> với dữ liệu đúng.
     */
    @Test
    public void testSearchGuests_WithPagination_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        List<Guest> guests = Arrays.asList(
            new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false"),
            new Guest("Tran Thi B", "1992-02-02", "987654321", "P654321", "Ho Chi Minh", "Viet Nam", "0987654321", "b@example.com", "false", "false")
        );
        Page<Guest> page = new PageImpl<>(guests, PageRequest.of(0, 10), 2);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(guestRepository.searchGuests(pageable, "%test%")).thenReturn(page);

        // Gọi phương thức
        Page<Guest> result = guestService.searchGuests(pageable, "test");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(guestRepository).searchGuests(pageable, "%test%");
    }

    /**
     * Test case TC-GUEST-SERVICE-005: Kiểm tra tìm kiếm guest không có pagination thành công.
     * Expected: Trả về List<Guest> với dữ liệu đúng.
     */
    @Test
    public void testSearchGuests_WithoutPagination_ShouldReturnList() {
        // Chuẩn bị dữ liệu test
        List<Guest> guests = Arrays.asList(
            new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false")
        );

        // Mock repository
        when(guestRepository.searchGuests("%test%")).thenReturn(guests);

        // Gọi phương thức
        List<Guest> result = guestService.searchGuests("test");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nguyen Van A", result.get(0).getGuestName());
        verify(guestRepository).searchGuests("%test%");
    }

    /**
     * Test case TC-GUEST-SERVICE-006: Kiểm tra tìm kiếm guest với cart (đã check-in) thành công.
     * Expected: Trả về guest đúng với idCard.
     */
    @Test
    public void testSearchGuestWithCart_ShouldReturnGuestByIdCard() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Mock repository
        when(guestRepository.searchGuestWithCart("123456789")).thenReturn(guest);

        // Gọi phương thức
        Guest result = guestService.searchGuestWithCart("123456789");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("123456789", result.getIdCard());
        verify(guestRepository).searchGuestWithCart("123456789");
    }

    /**
     * Test case TC-GUEST-SERVICE-007: Kiểm tra kiểm tra tồn tại guest - không tồn tại.
     * Expected: Trả về 0 khi guest không tồn tại.
     */
    @Test
    public void testCheckExistGuest_NotExist_ShouldReturn0() {
        // Mock repository
        when(guestRepository.checkExistGuest("123456789")).thenReturn(0);

        // Gọi phương thức
        Integer result = guestService.checkExistGuest("123456789");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(0), result);
        verify(guestRepository).checkExistGuest("123456789");
    }

    /**
     * Test case TC-GUEST-SERVICE-008: Kiểm tra kiểm tra tồn tại guest - tồn tại 1 bản ghi.
     * Expected: Trả về 1 khi guest tồn tại 1 bản ghi.
     */
    @Test
    public void testCheckExistGuest_ExistOne_ShouldReturn1() {
        // Mock repository
        when(guestRepository.checkExistGuest("123456789")).thenReturn(1);

        // Gọi phương thức
        Integer result = guestService.checkExistGuest("123456789");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(1), result);
        verify(guestRepository).checkExistGuest("123456789");
    }

    /**
     * Test case TC-GUEST-SERVICE-009: Kiểm tra kiểm tra tồn tại guest - duplicate.
     * Expected: Trả về 2 khi có duplicate trong DB.
     */
    @Test
    public void testCheckExistGuest_Duplicate_ShouldReturn2() {
        // Mock repository
        when(guestRepository.checkExistGuest("123456789")).thenReturn(2);

        // Gọi phương thức
        Integer result = guestService.checkExistGuest("123456789");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(2), result);
        verify(guestRepository).checkExistGuest("123456789");
    }

    /**
     * Test case TC-GUEST-SERVICE-010: Kiểm tra cập nhật thông tin complete cho guest cũ.
     * Expected: Gọi updateComplete với parameters đúng.
     */
    @Test
    public void testUpdateComplete_ShouldCallRepositoryUpdateComplete() {
        // Gọi phương thức
        guestService.updateComplete("P123456", "Ha Noi", "0123456789", "a@example.com", "true", "123456789");

        // Verify gọi updateComplete
        verify(guestRepository).updateComplete("P123456", "Ha Noi", "0123456789", "a@example.com", "true", "true", "123456789");
    }

    /**
     * Test case TC-GUEST-SERVICE-011: Kiểm tra cập nhật thông tin normal cho guest.
     * Expected: Gọi updateNomal với parameters đúng.
     */
    @Test
    public void testUpdateNomal_ShouldCallRepositoryUpdateNomal() {
        // Gọi phương thức
        guestService.updateNomal("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", 1L);

        // Verify gọi updateNomal
        verify(guestRepository).updateNomal("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", 1L);
    }

    /**
     * Test case TC-GUEST-SERVICE-012: Kiểm tra lấy thông tin guest theo số phòng.
     * Expected: Trả về guest đúng với chamberNumber.
     */
    @Test
    public void testGetGuestInfoByChamberNumber_ShouldReturnGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Mock repository
        when(guestRepository.getGuestInfoByChamberNumber("101")).thenReturn(guest);

        // Gọi phương thức
        Guest result = guestService.getGuestInfoByChamberNumber("101");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getGuestName());
        verify(guestRepository).getGuestInfoByChamberNumber("101");
    }

    /**
     * Test case TC-GUEST-SERVICE-013: Kiểm tra tìm kiếm với text rỗng.
     * Expected: Vẫn gọi repository với %%%.
     */
    @Test
    public void testSearchGuests_EmptyText_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test
        List<Guest> guests = Arrays.asList();
        Page<Guest> page = new PageImpl<>(guests, PageRequest.of(0, 10), 0);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(guestRepository.searchGuests(pageable, "%%")).thenReturn(page);

        // Gọi phương thức với text rỗng
        Page<Guest> result = guestService.searchGuests(pageable, "");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(guestRepository).searchGuests(pageable, "%%");
    }

    /**
     * Test case TC-GUEST-SERVICE-014: Kiểm tra tìm kiếm với text có khoảng trắng.
     * Expected: Trim text và thêm %.
     */
    @Test
    public void testSearchGuests_TextWithSpaces_ShouldTrimAndSearch() {
        // Chuẩn bị dữ liệu test
        List<Guest> guests = Arrays.asList(
            new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false")
        );
        Page<Guest> page = new PageImpl<>(guests, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(guestRepository.searchGuests(pageable, "%test%")).thenReturn(page);

        // Gọi phương thức với text có khoảng trắng
        Page<Guest> result = guestService.searchGuests(pageable, "  test  ");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(guestRepository).searchGuests(pageable, "%test%");
    }

    /**
     * Test case TC-GUEST-SERVICE-015: Kiểm tra searchGuestWithCart trả về null.
     * Expected: Xử lý đúng khi không tìm thấy guest.
     */
    @Test
    public void testSearchGuestWithCart_NotFound_ShouldReturnNull() {
        // Mock repository trả về null
        when(guestRepository.searchGuestWithCart("999999999")).thenReturn(null);

        // Gọi phương thức
        Guest result = guestService.searchGuestWithCart("999999999");

        // Kiểm tra kết quả
        assertNull(result);
        verify(guestRepository).searchGuestWithCart("999999999");
    }

    // Có thể thêm test cho các trường hợp exception, null parameters, etc.
}
