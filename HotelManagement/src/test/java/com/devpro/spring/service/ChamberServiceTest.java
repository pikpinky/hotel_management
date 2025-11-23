package com.devpro.spring.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.repository.ChamberRepository;

/**
 * Lớp test unit cho ChamberServiceImpl.
 * Test các chức năng quản lý phòng: tìm kiếm, thêm, cập nhật, xóa phòng.
 * Sử dụng Mockito để mock ChamberRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChamberServiceTest {

    @Mock
    private ChamberRepository chamberRepository;

    @InjectMocks
    private ChamberServiceImpl chamberService;

    /**
     * Test case TC-CHAMBER-SERVICE-001: Kiểm tra tìm chamber theo ID thành công.
     * Expected: Trả về chamber đúng với ID được truyền.
     */
    @Test
    public void testFindChamber_ShouldReturnChamberById() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");

        // Mock repository
        when(chamberRepository.getOne(1L)).thenReturn(chamber);

        // Gọi phương thức
        Chamber result = chamberService.findChamber(1L);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("101", result.getChamberNumber());
        verify(chamberRepository).getOne(1L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-002: Kiểm tra xóa chamber thành công.
     * Expected: Gọi deleteById trên repository với ID đúng.
     */
    @Test
    public void testDeleteChamber_ShouldCallRepositoryDelete() {
        // Gọi phương thức
        chamberService.deleteChamber(1L);

        // Verify gọi deleteById
        verify(chamberRepository).deleteById(1L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-003: Kiểm tra tìm kiếm chamber với price 1 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice1_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("101", "single", "true", "50", "20", "note1", "true"),
            new Chamber("102", "single", "false", "60", "25", "note2", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 2);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamberWithPrice1(pageable, "single", "true")).thenReturn(page);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "single", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(chamberRepository).searchChamberWithPrice1(pageable, "single", "true");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-004: Kiểm tra tìm kiếm chamber với price 2 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice2_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("201", "couple", "true", "100", "30", "note1", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamberWithPrice2(pageable, "couple", "true")).thenReturn(page);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice2(pageable, "couple", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chamberRepository).searchChamberWithPrice2(pageable, "couple", "true");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-005: Kiểm tra tìm kiếm chamber với price 3 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice3_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("301", "family", "true", "200", "50", "note1", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamberWithPrice3(pageable, "family", "true")).thenReturn(page);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice3(pageable, "family", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chamberRepository).searchChamberWithPrice3(pageable, "family", "true");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-006: Kiểm tra cập nhật check-in (đặt phòng thành không trống).
     * Expected: Gọi updateChamberIsEmpty với "false" và ID đúng.
     */
    @Test
    public void testUpdateCheckIn_ShouldUpdateChamberToOccupied() {
        // Gọi phương thức
        chamberService.updateCheckIn(1L);

        // Verify gọi updateChamberIsEmpty
        verify(chamberRepository).updateChamberIsEmpty("false", 1L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-007: Kiểm tra tìm kiếm chamber với text thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamber_WithText_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("101", "single", "true", "50", "20", "note", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamber(pageable, "%test%")).thenReturn(page);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamber(pageable, "test");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chamberRepository).searchChamber(pageable, "%test%");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-008: Kiểm tra cập nhật thông tin chamber thành công.
     * Expected: Gọi updateChamberInfo với parameters đúng.
     */
    @Test
    public void testUpdateChamberInfo_ShouldCallRepositoryUpdate() {
        // Gọi phương thức
        chamberService.updateChamberInfo("101", "single", "100", "20", "note", "true", 1L);

        // Verify gọi updateChamberInfo
        verify(chamberRepository).updateChamberInfo("101", "single", "100", "20", "note", "true", 1L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-009: Kiểm tra thêm chamber mới thành công.
     * Expected: Tạo Chamber mới với isEmpty = "true" và gọi save.
     */
    @Test
    public void testAddChamber_ShouldCreateNewChamber() {
        // Gọi phương thức
        chamberService.addChamber("101", "single", "100", "20", "note", "true");

        // Verify gọi save với Chamber có isEmpty = "true"
        verify(chamberRepository).save(argThat(chamber -> {
            assertEquals("101", chamber.getChamberNumber());
            assertEquals("single", chamber.getChamberType());
            assertEquals("100", chamber.getPriceDay());
            assertEquals("20", chamber.getChamberArea());
            assertEquals("note", chamber.getNote());
            assertEquals("true", chamber.getIsVip());
            assertEquals("true", chamber.getIsEmpty()); // Mặc định là trống
            return true;
        }));
    }

    /**
     * Test case TC-CHAMBER-SERVICE-010: Kiểm tra thêm chamber thường (không VIP) thành công.
     * Expected: Tạo Chamber với isVip = "false".
     */
    @Test
    public void testAddChamber_NormalChamber_ShouldSetVipFalse() {
        // Gọi phương thức với fvip = "false"
        chamberService.addChamber("102", "couple", "150", "30", "note", "false");

        // Verify gọi save với isVip = "false"
        verify(chamberRepository).save(argThat(chamber -> {
            assertEquals("false", chamber.getIsVip());
            assertEquals("true", chamber.getIsEmpty());
            return true;
        }));
    }

    /**
     * Test case TC-CHAMBER-SERVICE-011: Kiểm tra tìm kiếm với text rỗng.
     * Expected: Vẫn gọi repository với %%text%%.
     */
    @Test
    public void testSearchChamber_EmptyText_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList();
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 0);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamber(pageable, "%%")).thenReturn(page);

        // Gọi phương thức với text rỗng
        Page<Chamber> result = chamberService.searchChamber(pageable, "");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(chamberRepository).searchChamber(pageable, "%%");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-012: Kiểm tra tìm kiếm với text có khoảng trắng.
     * Expected: Trim text và thêm %.
     */
    @Test
    public void testSearchChamber_TextWithSpaces_ShouldTrimAndSearch() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("101", "single", "true", "50", "20", "note", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock repository
        when(chamberRepository.searchChamber(pageable, "%test%")).thenReturn(page);

        // Gọi phương thức với text có khoảng trắng
        Page<Chamber> result = chamberService.searchChamber(pageable, "  test  ");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chamberRepository).searchChamber(pageable, "%test%");
    }

    // Có thể thêm test cho các trường hợp exception, null parameters, etc.
}