package com.devpro.spring.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.model.Guest;
import com.devpro.spring.model.Rental;
import com.devpro.spring.repository.RentalRepository;

/**
 * Lớp test unit cho RentalServiceImpl.
 * Test các chức năng quản lý rental: thêm rental, lấy thông tin check-out, tính tiền.
 * Sử dụng Mockito để mock RentalRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    /**
     * Test case TC-RENTAL-SERVICE-001: Kiểm tra thêm rental thành công.
     * Expected: Gọi save trên repository với rental đúng.
     */
    @Test
    public void testAddRentalInfo_ShouldSaveRental() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "false");
        Set<Chamber> chambers = new HashSet<>();
        chambers.add(chamber);

        Rental rental = new Rental();
        rental.setGuest(guest);
        rental.setChambers(chambers);
        rental.setNote("Test rental");

        // Gọi phương thức
        rentalService.addRentalInfo(rental);

        // Verify gọi save
        verify(rentalRepository).save(rental);
    }

    /**
     * Test case TC-RENTAL-SERVICE-002: Kiểm tra lấy danh sách chamber có order food thành công.
     * Expected: Trả về List<String> với chamber numbers.
     */
    @Test
    public void testGetListChamberOrderFood_ShouldReturnChamberNumbers() {
        // Chuẩn bị dữ liệu test
        List<String> chamberNumbers = Arrays.asList("101", "102", "103");

        // Mock repository
        when(rentalRepository.getListChamberOrderFood()).thenReturn(chamberNumbers);

        // Gọi phương thức
        List<String> result = rentalService.getListChamberOrderFood();

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("101", result.get(0));
        verify(rentalRepository).getListChamberOrderFood();
    }

    /**
     * Test case TC-RENTAL-SERVICE-003: Kiểm tra lấy rental ID để order food thành công.
     * Expected: Trả về rental ID dưới dạng String.
     */
    @Test
    public void testGetRentalIdOrderFood_ShouldReturnRentalId() {
        // Mock repository
        when(rentalRepository.getRentalIdOrderFood("101")).thenReturn("1");

        // Gọi phương thức
        String result = rentalService.getRentalIdOrderFood("101");

        // Kiểm tra kết quả
        assertEquals("1", result);
        verify(rentalRepository).getRentalIdOrderFood("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-004: Kiểm tra tìm rental theo ID thành công.
     * Expected: Trả về rental đúng với ID.
     */
    @Test
    public void testGetRentalById_ShouldReturnRental() {
        // Chuẩn bị dữ liệu test
        Rental rental = new Rental();
        rental.setNote("Test rental");

        // Mock repository
        when(rentalRepository.getOne(1L)).thenReturn(rental);

        // Gọi phương thức
        Rental result = rentalService.getRentalById(1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Test rental", result.getNote());
        verify(rentalRepository).getOne(1L);
    }

    /**
     * Test case TC-RENTAL-SERVICE-005: Kiểm tra tính tổng tiền food thành công.
     * Expected: Trả về tổng tiền food dưới dạng Integer.
     */
    @Test
    public void testGetCheckTotalFoodPrice_ShouldReturnTotalPrice() {
        // Mock repository
        when(rentalRepository.getCheckTotalFoodPrice("101")).thenReturn(500000);

        // Gọi phương thức
        Integer result = rentalService.getCheckTotalFoodPrice("101");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(500000), result);
        verify(rentalRepository).getCheckTotalFoodPrice("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-006: Kiểm tra tính tổng tiền service thành công.
     * Expected: Trả về tổng tiền service dưới dạng Integer.
     */
    @Test
    public void testGetCheckTotalServicePrice_ShouldReturnTotalPrice() {
        // Mock repository
        when(rentalRepository.getCheckTotalServicePrice("101")).thenReturn(300000);

        // Gọi phương thức
        Integer result = rentalService.getCheckTotalServicePrice("101");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(300000), result);
        verify(rentalRepository).getCheckTotalServicePrice("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-007: Kiểm tra tính số ngày ở thành công.
     * Expected: Trả về số ngày ở dưới dạng Integer.
     */
    @Test
    public void testGetNumberDaysStay_ShouldReturnDays() {
        // Mock repository
        when(rentalRepository.getNumberDaysStay("101")).thenReturn(3);

        // Gọi phương thức
        Integer result = rentalService.getNumberDaysStay("101");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(3), result);
        verify(rentalRepository).getNumberDaysStay("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-008: Kiểm tra lấy thông tin rental check-out thành công.
     * Expected: Trả về rental với thông tin check-out.
     */
    @Test
    public void testGetRentalCheckOutInfo_ShouldReturnRental() {
        // Chuẩn bị dữ liệu test
        Rental rental = new Rental();
        rental.setNote("Check-out rental");

        // Mock repository
        when(rentalRepository.getRentalCheckOutInfo("101")).thenReturn(rental);

        // Gọi phương thức
        Rental result = rentalService.getRentalCheckOutInfo("101");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Check-out rental", result.getNote());
        verify(rentalRepository).getRentalCheckOutInfo("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-009: Kiểm tra lấy thông tin guest check-out thành công.
     * Expected: Trả về guest với thông tin check-out.
     */
    @Test
    public void testGetGuestCheckOutInfo_ShouldReturnGuest() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");

        // Mock repository
        when(rentalRepository.getGuestCheckOutInfo("101")).thenReturn(guest);

        // Gọi phương thức
        Guest result = rentalService.getGuestCheckOutInfo("101");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getGuestName());
        verify(rentalRepository).getGuestCheckOutInfo("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-010: Kiểm tra lấy thông tin chamber check-out thành công.
     * Expected: Trả về chamber với thông tin check-out.
     */
    @Test
    public void testGetChamberCheckOutInfo_ShouldReturnChamber() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "false");

        // Mock repository
        when(rentalRepository.getChamberCheckOutInfo("101")).thenReturn(chamber);

        // Gọi phương thức
        Chamber result = rentalService.getChamberCheckOutInfo("101");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("101", result.getChamberNumber());
        verify(rentalRepository).getChamberCheckOutInfo("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-011: Kiểm tra với chamber number không tồn tại.
     * Expected: Xử lý đúng khi không tìm thấy dữ liệu.
     */
    @Test
    public void testGetRentalIdOrderFood_NotFound_ShouldReturnNull() {
        // Mock repository trả về null
        when(rentalRepository.getRentalIdOrderFood("999")).thenReturn(null);

        // Gọi phương thức
        String result = rentalService.getRentalIdOrderFood("999");

        // Kiểm tra kết quả
        assertNull(result);
        verify(rentalRepository).getRentalIdOrderFood("999");
    }

    /**
     * Test case TC-RENTAL-SERVICE-012: Kiểm tra danh sách chamber rỗng.
     * Expected: Trả về list rỗng.
     */
    @Test
    public void testGetListChamberOrderFood_EmptyList_ShouldReturnEmpty() {
        // Chuẩn bị dữ liệu test
        List<String> emptyList = Arrays.asList();

        // Mock repository
        when(rentalRepository.getListChamberOrderFood()).thenReturn(emptyList);

        // Gọi phương thức
        List<String> result = rentalService.getListChamberOrderFood();

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(rentalRepository).getListChamberOrderFood();
    }

    /**
     * Test case TC-RENTAL-SERVICE-013: Kiểm tra tổng tiền food bằng 0.
     * Expected: Trả về 0 khi không có food order.
     */
    @Test
    public void testGetCheckTotalFoodPrice_NoFood_ShouldReturnZero() {
        // Mock repository
        when(rentalRepository.getCheckTotalFoodPrice("101")).thenReturn(0);

        // Gọi phương thức
        Integer result = rentalService.getCheckTotalFoodPrice("101");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(0), result);
        verify(rentalRepository).getCheckTotalFoodPrice("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-014: Kiểm tra số ngày ở bằng 0.
     * Expected: Trả về 0 khi chưa có ngày check-out.
     */
    @Test
    public void testGetNumberDaysStay_ZeroDays_ShouldReturnZero() {
        // Mock repository
        when(rentalRepository.getNumberDaysStay("101")).thenReturn(0);

        // Gọi phương thức
        Integer result = rentalService.getNumberDaysStay("101");

        // Kiểm tra kết quả
        assertEquals(Integer.valueOf(0), result);
        verify(rentalRepository).getNumberDaysStay("101");
    }

    /**
     * Test case TC-RENTAL-SERVICE-015: Kiểm tra rental với nhiều chambers.
     * Expected: Rental có thể chứa nhiều chambers.
     */
    @Test
    public void testAddRentalInfo_WithMultipleChambers_ShouldSaveCorrectly() {
        // Chuẩn bị dữ liệu test
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Ha Noi", "Viet Nam", "0123456789", "a@example.com", "false", "false");
        Chamber chamber1 = new Chamber("101", "single", "true", "100", "20", "note1", "false");
        Chamber chamber2 = new Chamber("102", "single", "true", "100", "20", "note2", "false");
        Set<Chamber> chambers = new HashSet<>();
        chambers.add(chamber1);
        chambers.add(chamber2);

        Rental rental = new Rental();
        rental.setGuest(guest);
        rental.setChambers(chambers);
        rental.setNote("Multiple chambers rental");

        // Gọi phương thức
        rentalService.addRentalInfo(rental);

        // Verify gọi save với rental có 2 chambers
        verify(rentalRepository).save(argThat(r -> {
            assertEquals(2, r.getChambers().size());
            assertTrue(r.getChambers().contains(chamber1));
            assertTrue(r.getChambers().contains(chamber2));
            return true;
        }));
    }

    // Có thể thêm test cho các trường hợp exception, null parameters, etc.
}
