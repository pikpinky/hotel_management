package com.devpro.spring.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.service.ChamberService;

/**
 * Lớp test unit cho CheckInController.
 * Test các chức năng hiển thị trang check-in với pagination và filter theo giá, loại phòng, VIP.
 * Sử dụng Mockito để mock ChamberService.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckInControllerTest {

    @Mock
    private ChamberService chamberService;

    @Mock
    private Model model;

    @InjectMocks
    private CheckInController checkInController;

    /**
     * Test case TC-CHECKIN-CONTROLLER-001: Kiểm tra hiển thị trang check-in với filter giá 1 (thấp).
     * Expected: Gọi searchChamberWithPrice1 và trả về view "check-in".
     */
    @Test
    public void testCheckIn_PriceFilter1_ShouldCallSearchChamberWithPrice1() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("101", "single", "true", "50", "20", "note1", "true"),
            new Chamber("102", "single", "false", "60", "25", "note2", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 2);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), eq("single"), eq("true"))).thenReturn(page);

        // Gọi phương thức
        String viewName = checkInController.checkIn(model, 0, 1, "single", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
        verify(chamberService).searchChamberWithPrice1(any(Pageable.class), eq("single"), eq("true"));
        verify(model).addAttribute("checkPrice1", true);
        verify(model).addAttribute("checkType1", true);
        verify(model).addAttribute("checkVip1", true);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-002: Kiểm tra hiển thị trang check-in với filter giá 2 (trung bình).
     * Expected: Gọi searchChamberWithPrice2 và trả về view "check-in".
     */
    @Test
    public void testCheckIn_PriceFilter2_ShouldCallSearchChamberWithPrice2() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("201", "couple", "true", "100", "30", "note1", "true"),
            new Chamber("202", "couple", "false", "120", "35", "note2", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 2);

        // Mock service
        when(chamberService.searchChamberWithPrice2(any(Pageable.class), eq("couple"), eq("false"))).thenReturn(page);

        // Gọi phương thức
        String viewName = checkInController.checkIn(model, 0, 2, "couple", "false");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
        verify(chamberService).searchChamberWithPrice2(any(Pageable.class), eq("couple"), eq("false"));
        verify(model).addAttribute("checkPrice2", true);
        verify(model).addAttribute("checkType2", true);
        verify(model).addAttribute("checkVip2", true);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-003: Kiểm tra hiển thị trang check-in với filter giá 3 (cao).
     * Expected: Gọi searchChamberWithPrice3 và trả về view "check-in".
     */
    @Test
    public void testCheckIn_PriceFilter3_ShouldCallSearchChamberWithPrice3() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("301", "family", "true", "200", "50", "note1", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice3(any(Pageable.class), eq("family"), eq("true"))).thenReturn(page);

        // Gọi phương thức
        String viewName = checkInController.checkIn(model, 0, 3, "family", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
        verify(chamberService).searchChamberWithPrice3(any(Pageable.class), eq("family"), eq("true"));
        verify(model).addAttribute("checkPrice3", true);
        verify(model).addAttribute("checkType3", true);
        verify(model).addAttribute("checkVip1", true);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-004: Kiểm tra pagination với trang đầu tiên.
     * Expected: Tính toán pagination đúng cho trang đầu.
     */
    @Test
    public void testCheckIn_PaginationFirstPage_ShouldCalculateCorrectPagination() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify pagination attributes
        verify(model).addAttribute("checkPrice1", true);
        verify(model).addAttribute("checkPrice2", false);
        verify(model).addAttribute("checkPrice3", false);
        verify(model).addAttribute("checkType1", true);
        verify(model).addAttribute("checkType2", false);
        verify(model).addAttribute("checkType3", false);
        verify(model).addAttribute("checkVip1", true);
        verify(model).addAttribute("checkVip2", false);
        verify(model).addAttribute("currentPrice", 1);
        verify(model).addAttribute("currentType", "single");
        verify(model).addAttribute("currentVip", "true");
        verify(model).addAttribute("beginIndex", 1L);
        verify(model).addAttribute("endIndex", 1L);
        verify(model).addAttribute("currentIndex", 1);
        verify(model).addAttribute("totalPageCount", 1L);
        verify(model).addAttribute("totalElement", 1L);
        verify(model).addAttribute("extra", false);
        verify(model).addAttribute("checkLast", false);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-005: Kiểm tra pagination với nhiều trang.
     * Expected: Tính toán pagination đúng cho nhiều trang.
     */
    @Test
    public void testCheckIn_PaginationMultiplePages_ShouldCalculateCorrectRange() {
        // Chuẩn bị dữ liệu test - giả sử có 10 trang
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(4, 12), 120); // Trang 5 (index 4), tổng 10 trang

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 4, 1, "single", "true");

        // Verify pagination attributes
        verify(model).addAttribute("checkPrice1", true);
        verify(model).addAttribute("checkPrice2", false);
        verify(model).addAttribute("checkPrice3", false);
        verify(model).addAttribute("checkType1", true);
        verify(model).addAttribute("checkType2", false);
        verify(model).addAttribute("checkType3", false);
        verify(model).addAttribute("checkVip1", true);
        verify(model).addAttribute("checkVip2", false);
        verify(model).addAttribute("currentPrice", 1);
        verify(model).addAttribute("currentType", "single");
        verify(model).addAttribute("currentVip", "true");
        verify(model).addAttribute("beginIndex", 1L);
        verify(model).addAttribute("endIndex", 10L);
        verify(model).addAttribute("currentIndex", 5);
        verify(model).addAttribute("totalPageCount", 10L);
        verify(model).addAttribute("totalElement", 120L);
        verify(model).addAttribute("chambers", page);
        verify(model).addAttribute("baseUrl", "/check-in?page=");
        verify(model).addAttribute("filterUrl", "&p=1&t=single&v=true");
        verify(model).addAttribute("extra", false);
        verify(model).addAttribute("checkLast", false);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-006: Kiểm tra filter loại phòng single.
     * Expected: Set checkType1 = true.
     */
    @Test
    public void testCheckIn_TypeFilterSingle_ShouldSetCheckType1() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), eq("single"), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify type filter
        verify(model).addAttribute("checkType1", true);
        verify(model).addAttribute("checkType2", false);
        verify(model).addAttribute("checkType3", false);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-007: Kiểm tra filter loại phòng couple.
     * Expected: Set checkType2 = true.
     */
    @Test
    public void testCheckIn_TypeFilterCouple_ShouldSetCheckType2() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("201", "couple", "true", "100", "30", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice2(any(Pageable.class), eq("couple"), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 2, "couple", "true");

        // Verify type filter
        verify(model).addAttribute("checkType1", false);
        verify(model).addAttribute("checkType2", true);
        verify(model).addAttribute("checkType3", false);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-008: Kiểm tra filter loại phòng family.
     * Expected: Set checkType3 = true.
     */
    @Test
    public void testCheckIn_TypeFilterFamily_ShouldSetCheckType3() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("301", "family", "true", "200", "50", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice3(any(Pageable.class), eq("family"), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 3, "family", "true");

        // Verify type filter
        verify(model).addAttribute("checkType1", false);
        verify(model).addAttribute("checkType2", false);
        verify(model).addAttribute("checkType3", true);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-009: Kiểm tra filter VIP true.
     * Expected: Set checkVip1 = true.
     */
    @Test
    public void testCheckIn_VipFilterTrue_ShouldSetCheckVip1() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), eq("true"))).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify VIP filter
        verify(model).addAttribute("checkVip1", true);
        verify(model).addAttribute("checkVip2", false);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-010: Kiểm tra filter VIP false.
     * Expected: Set checkVip2 = true.
     */
    @Test
    public void testCheckIn_VipFilterFalse_ShouldSetCheckVip2() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "false", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), eq("false"))).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "false");

        // Verify VIP filter
        verify(model).addAttribute("checkVip1", false);
        verify(model).addAttribute("checkVip2", true);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-011: Kiểm tra set current values vào model.
     * Expected: Set currentPrice, currentType, currentVip đúng.
     */
    @Test
    public void testCheckIn_ShouldSetCurrentValues() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify current values
        verify(model).addAttribute("currentPrice", 1);
        verify(model).addAttribute("currentType", "single");
        verify(model).addAttribute("currentVip", "true");
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-012: Kiểm tra set chambers và pagination info.
     * Expected: Set chambers, totalElement, baseUrl, filterUrl đúng.
     */
    @Test
    public void testCheckIn_ShouldSetChambersAndPaginationInfo() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(
            new Chamber("101", "single", "true", "50", "20", "note1", "true"),
            new Chamber("102", "single", "true", "60", "25", "note2", "true")
        );
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 2);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), eq("single"), eq("true"))).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify chambers and pagination
        verify(model).addAttribute("chambers", page);
        verify(model).addAttribute("totalElement", 2L);
        verify(model).addAttribute("baseUrl", "/check-in?page=");
        verify(model).addAttribute("filterUrl", "&p=1&t=single&v=true");
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-013: Kiểm tra với trang rỗng.
     * Expected: Xử lý đúng khi không có chambers.
     */
    @Test
    public void testCheckIn_EmptyPage_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test - trang rỗng
        Page<Chamber> page = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 12), 0);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), anyString())).thenReturn(page);

        // Gọi phương thức
        String viewName = checkInController.checkIn(model, 0, 1, "single", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
        verify(model).addAttribute("totalElement", 0L);
        verify(model).addAttribute("beginIndex", 1L);
        verify(model).addAttribute("endIndex", 1L);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-014: Kiểm tra với default parameters.
     * Expected: Sử dụng giá trị mặc định khi không truyền parameters.
     */
    @Test
    public void testCheckIn_DefaultParameters_ShouldUseDefaults() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service với default values
        when(chamberService.searchChamberWithPrice2(any(Pageable.class), eq("single"), eq("true"))).thenReturn(page);

        // Gọi phương thức với default values
        String viewName = checkInController.checkIn(model, 0, 2, "single", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
        verify(chamberService).searchChamberWithPrice2(any(Pageable.class), eq("single"), eq("true"));
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-015: Kiểm tra với page size 12.
     * Expected: Pageable được tạo với size 12.
     */
    @Test
    public void testCheckIn_PageSize_ShouldBe12() {
        // Chuẩn bị dữ liệu test
        List<Chamber> chambers = Arrays.asList(new Chamber("101", "single", "true", "50", "20", "note", "true"));
        Page<Chamber> page = new PageImpl<>(chambers, PageRequest.of(0, 12), 1);

        // Mock service
        when(chamberService.searchChamberWithPrice1(any(Pageable.class), anyString(), anyString())).thenReturn(page);

        // Gọi phương thức
        checkInController.checkIn(model, 0, 1, "single", "true");

        // Verify Pageable được tạo đúng
        verify(chamberService).searchChamberWithPrice1(argThat(pageable -> pageable.getPageSize() == 12), anyString(), anyString());
    }

    // Có thể thêm test cho các trường hợp edge case khác như page number âm, etc.
}
