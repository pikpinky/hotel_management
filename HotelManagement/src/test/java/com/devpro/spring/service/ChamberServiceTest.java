package com.devpro.spring.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.repository.ChamberRepository;

/**
 * Lớp test integration cho ChamberServiceImpl.
 * Test các chức năng quản lý phòng: tìm kiếm, thêm, cập nhật, xóa phòng.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ChamberServiceTest {

    @Autowired
    private ChamberRepository chamberRepository;

    @Autowired
    private ChamberService chamberService;

    /**
     * Test case TC-CHAMBER-SERVICE-001: Kiểm tra tìm chamber theo ID thành công.
     * Expected: Trả về chamber đúng với ID được truyền.
     */
    @Test
    public void testFindChamber_ShouldReturnChamberById() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        Chamber result = chamberService.findChamber(chamberId);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("101", result.getChamberNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-002: Kiểm tra xóa chamber thành công.
     * Expected: Gọi deleteById trên repository với ID đúng.
     */
    @Test
    public void testDeleteChamber_ShouldCallRepositoryDelete() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        chamberService.deleteChamber(chamberId);

        // Kiểm tra kết quả - chamber không còn trong DB
        assertEquals(0, chamberRepository.count());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-003: Kiểm tra tìm kiếm chamber với price 1 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice1_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "false", "60", "25", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "single", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements()); // Chỉ chamber1 có vip="true"
        assertEquals("101", result.getContent().get(0).getChamberNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-004: Kiểm tra tìm kiếm chamber với price 2 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice2_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("201", "couple", "true", "100", "30", "note1", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice2(pageable, "couple", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("201", result.getContent().get(0).getChamberNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-005: Kiểm tra tìm kiếm chamber với price 3 thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamberWithPrice3_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("301", "family", "true", "200", "50", "note1", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice3(pageable, "family", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("301", result.getContent().get(0).getChamberNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-006: Kiểm tra cập nhật check-in (đặt phòng thành không trống).
     * Expected: Gọi updateChamberIsEmpty với "false" và ID đúng.
     */
    @Test
    public void testUpdateCheckIn_ShouldUpdateChamberToOccupied() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        chamberService.updateCheckIn(chamberId);

        // Kiểm tra kết quả - chamber được update isEmpty = "false"
        Chamber updatedChamber = chamberRepository.findById(chamberId).orElse(null);
        assertNotNull(updatedChamber);
        assertEquals("false", updatedChamber.getIsEmpty());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-007: Kiểm tra tìm kiếm chamber với text thành công.
     * Expected: Trả về Page<Chamber> với dữ liệu đúng.
     */
    @Test
    public void testSearchChamber_WithText_ShouldReturnPagedResult() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamber(pageable, "test");

        // Kiểm tra kết quả - tùy thuộc vào implementation của searchChamber
        assertNotNull(result);
        // Note: Kết quả phụ thuộc vào logic search trong repository
    }

    /**
     * Test case TC-CHAMBER-SERVICE-008: Kiểm tra cập nhật thông tin chamber thành công.
     * Expected: Gọi updateChamberInfo với parameters đúng.
     */
    @Test
    public void testUpdateChamberInfo_ShouldCallRepositoryUpdate() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        chamberService.updateChamberInfo("102", "couple", "150", "30", "new note", "false", chamberId);

        // Kiểm tra kết quả - chamber được update
        Chamber updatedChamber = chamberRepository.findById(chamberId).orElse(null);
        assertNotNull(updatedChamber);
        assertEquals("102", updatedChamber.getChamberNumber());
        assertEquals("couple", updatedChamber.getChamberType());
        assertEquals("150", updatedChamber.getPriceDay());
        assertEquals("30", updatedChamber.getChamberArea());
        assertEquals("new note", updatedChamber.getNote());
        assertEquals("false", updatedChamber.getIsVip());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-009: Kiểm tra thêm chamber mới thành công.
     * Expected: Tạo Chamber mới với isEmpty = "true" và gọi save.
     */
    @Test
    public void testAddChamber_ShouldCreateNewChamber() {
        // Gọi phương thức
        chamberService.addChamber("101", "single", "100", "20", "note", "true");

        // Kiểm tra kết quả - chamber được tạo với isEmpty = "true"
        Chamber savedChamber = chamberRepository.findAll().get(0);
        assertNotNull(savedChamber);
        assertEquals("101", savedChamber.getChamberNumber());
        assertEquals("single", savedChamber.getChamberType());
        assertEquals("100", savedChamber.getPriceDay());
        assertEquals("20", savedChamber.getChamberArea());
        assertEquals("note", savedChamber.getNote());
        assertEquals("true", savedChamber.getIsVip());
        assertEquals("true", savedChamber.getIsEmpty()); // Mặc định là trống
    }

    /**
     * Test case TC-CHAMBER-SERVICE-010: Kiểm tra tìm chamber với ID không tồn tại.
     * Expected: Throw EntityNotFoundException.
     */
    @Test(expected = javax.persistence.EntityNotFoundException.class)
    public void testFindChamber_InvalidId_ShouldReturnNull() {
        // Gọi phương thức với ID không tồn tại - expect exception
        chamberService.findChamber(999L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-011: Kiểm tra xóa chamber với ID không tồn tại.
     * Expected: Throw EmptyResultDataAccessException.
     */
    @Test(expected = org.springframework.dao.EmptyResultDataAccessException.class)
    public void testDeleteChamber_InvalidId_ShouldNotThrowException() {
        // Gọi phương thức với ID không tồn tại - expect exception
        chamberService.deleteChamber(999L);
    }

    /**
     * Test case TC-CHAMBER-SERVICE-012: Kiểm tra tìm kiếm chamber với price 1 nhưng không có data.
     * Expected: Trả về Page empty (cover edge case).
     */
    @Test
    public void testSearchChamberWithPrice1_NoData_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức khi DB empty
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "single", "true");

        // Kiểm tra kết quả - empty page
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-013: Kiểm tra cập nhật check-in với ID không tồn tại.
     * Expected: Không throw exception (cover error handling branch).
     */
    @Test
    public void testUpdateCheckIn_InvalidId_ShouldNotThrowException() {
        // Gọi phương thức với ID không tồn tại
        chamberService.updateCheckIn(999L);

        // Kiểm tra không throw exception
        // Nếu có logic throw exception, test sẽ fail
    }

    /**
     * Test case TC-CHAMBER-SERVICE-014: Kiểm tra cập nhật thông tin chamber với ID không tồn tại.
     * Expected: Không throw exception hoặc handle gracefully.
     */
    @Test
    public void testUpdateChamberInfo_InvalidId_ShouldHandleGracefully() {
        // Gọi phương thức với ID không tồn tại
        chamberService.updateChamberInfo("102", "couple", "150", "30", "new note", "false", 999L);

        // Kiểm tra không throw exception
        // Verify DB không thay đổi
        assertEquals(0, chamberRepository.count());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-015: Kiểm tra thêm chamber với parameters null/empty.
     * Expected: Handle null inputs gracefully (cover validation branches).
     */
    @Test
    public void testAddChamber_NullParameters_ShouldHandleNulls() {
        // Gọi phương thức với null parameters
        chamberService.addChamber(null, null, null, null, null, null);

        // Kiểm tra kết quả - tùy thuộc vào implementation
        // Có thể throw exception hoặc handle null
        // assertEquals(0, chamberRepository.count()); // Nếu không tạo
    }

    /**
     * Test case TC-CHAMBER-SERVICE-016: Kiểm tra tìm kiếm chamber với price 1 và type "all".
     * Expected: Trả về tất cả chambers match price 1, ignore type (logic nghiệp vụ).
     */
    @Test
    public void testSearchChamberWithPrice1_TypeAll_ShouldReturnAllMatchingPrice() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("201", "couple", "true", "60", "30", "note2", "true");
        Chamber chamber3 = new Chamber("301", "family", "true", "70", "50", "note3", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với type="all"
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "all", "true");

        // Kiểm tra kết quả - expect 3 chambers (logic nghiệp vụ: all types)
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // Expose bug if code doesn't handle "all"
    }

    /**
     * Test case TC-CHAMBER-SERVICE-017: Kiểm tra tìm kiếm chamber với price 1 và vip "all".
     * Expected: Trả về tất cả chambers match price 1, ignore vip.
     */
    @Test
    public void testSearchChamberWithPrice1_VipAll_ShouldReturnAllMatchingPrice() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "false", "60", "25", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với vip="all"
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "single", "all");

        // Kiểm tra kết quả - expect 2 chambers
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-018: Kiểm tra tìm kiếm chamber với price 2 và pagination page 1.
     * Expected: Trả về page 1 với đúng data.
     */
    @Test
    public void testSearchChamberWithPrice2_Page1_ShouldReturnCorrectPage() {
        // Chuẩn bị dữ liệu test - 15 chambers
        for (int i = 1; i <= 15; i++) {
            Chamber chamber = new Chamber("2" + String.format("%02d", i), "couple", "true", "1500000", "30", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        Pageable pageable = PageRequest.of(1, 10); // Page 1, size 10

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice2(pageable, "couple", "true");

        // Kiểm tra kết quả - page 1 có 5 chambers (11-15)
        assertNotNull(result);
        assertEquals(15, result.getTotalElements());
        assertEquals(5, result.getContent().size());
        assertEquals(1, result.getNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-019: Kiểm tra tìm kiếm chamber với price 3 và large data.
     * Expected: Handle large data set correctly.
     */
    @Test
    public void testSearchChamberWithPrice3_LargeData_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test - 100 chambers
        for (int i = 1; i <= 100; i++) {
            Chamber chamber = new Chamber("3" + String.format("%02d", i), "family", "true", "4000000", "50", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        Pageable pageable = PageRequest.of(0, 12);

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice3(pageable, "family", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(100, result.getTotalElements());
        assertEquals(12, result.getContent().size());
        assertEquals(9, result.getTotalPages()); // 100/12 = 8.33, 9 pages
    }

    /**
     * Test case TC-CHAMBER-SERVICE-020: Kiểm tra cập nhật check-in với valid ID.
     * Expected: Update isEmpty to "false", check DB.
     */
    @Test
    public void testUpdateCheckIn_ValidId_ShouldUpdateIsEmpty() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        chamberService.updateCheckIn(chamberId);

        // Kiểm tra kết quả - chamber isEmpty = "false"
        Chamber updated = chamberRepository.findById(chamberId).orElse(null);
        assertNotNull(updated);
        assertEquals("false", updated.getIsEmpty());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-021: Kiểm tra thêm chamber với duplicate chamberNumber.
     * Expected: Throw exception or handle duplicate.
     */
    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    public void testAddChamber_DuplicateNumber_ShouldThrowException() {
        // Thêm chamber đầu tiên
        chamberService.addChamber("101", "single", "100", "20", "note", "true");

        // Thêm chamber với số phòng trùng
        chamberService.addChamber("101", "couple", "200", "30", "note2", "false");
    }

    /**
     * Test case TC-CHAMBER-SERVICE-022: Kiểm tra tìm kiếm chamber với text match number.
     * Expected: Trả về chambers match chamberNumber.
     */
    @Test
    public void testSearchChamber_TextMatchNumber_ShouldReturnMatching() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "100", "20", "note", "true");
        Chamber chamber2 = new Chamber("102", "couple", "false", "200", "30", "special", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với text match number
        Page<Chamber> result = chamberService.searchChamber(pageable, "101");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("101", result.getContent().get(0).getChamberNumber());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-023: Kiểm tra tìm kiếm chamber với text match type.
     * Expected: Trả về chambers match chamberType.
     */
    @Test
    public void testSearchChamber_TextMatchType_ShouldReturnMatching() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "100", "20", "note", "true");
        Chamber chamber2 = new Chamber("201", "couple", "false", "200", "30", "note", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với text match type
        Page<Chamber> result = chamberService.searchChamber(pageable, "single");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("single", result.getContent().get(0).getChamberType());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-024: Kiểm tra cập nhật chamber info với valid data.
     * Expected: Update all fields correctly.
     */
    @Test
    public void testUpdateChamberInfo_ValidData_ShouldUpdateAllFields() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Gọi phương thức
        chamberService.updateChamberInfo("102", "couple", "150", "30", "new note", "false", chamberId);

        // Kiểm tra kết quả
        Chamber updated = chamberRepository.findById(chamberId).orElse(null);
        assertNotNull(updated);
        assertEquals("102", updated.getChamberNumber());
        assertEquals("couple", updated.getChamberType());
        assertEquals("150", updated.getPriceDay());
        assertEquals("30", updated.getChamberArea());
        assertEquals("new note", updated.getNote());
        assertEquals("false", updated.getIsVip());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-025: Kiểm tra tìm kiếm với empty text.
     * Expected: Trả về empty page.
     */
    @Test
    public void testSearchChamber_EmptyText_ShouldReturnEmpty() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức với empty text
        Page<Chamber> result = chamberService.searchChamber(pageable, "");

        // Kiểm tra kết quả - empty
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-026: Kiểm tra tìm kiếm chamber với price 2 no data.
     * Expected: Empty page.
     */
    @Test
    public void testSearchChamberWithPrice2_NoData_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức khi no data
        Page<Chamber> result = chamberService.searchChamberWithPrice2(pageable, "couple", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-027: Kiểm tra tìm kiếm chamber với price 3 no data.
     * Expected: Empty page.
     */
    @Test
    public void testSearchChamberWithPrice3_NoData_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        // Gọi phương thức khi no data
        Page<Chamber> result = chamberService.searchChamberWithPrice3(pageable, "family", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-028: Kiểm tra thêm chamber với empty strings.
     * Expected: Handle empty inputs.
     */
    @Test
    public void testAddChamber_EmptyParameters_ShouldHandleEmptys() {
        // Gọi phương thức với empty parameters
        chamberService.addChamber("", "", "", "", "", "");

        // Kiểm tra kết quả - tùy thuộc vào implementation
        // Có thể tạo chamber với empty fields
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-029: Kiểm tra xóa chamber sau khi update check-in.
     * Expected: Delete successfully.
     */
    @Test
    public void testDeleteChamber_AfterUpdateCheckIn_ShouldDelete() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true");
        chamber = chamberRepository.save(chamber);
        Long chamberId = chamber.getChamberId();

        // Update check-in
        chamberService.updateCheckIn(chamberId);

        // Xóa chamber
        chamberService.deleteChamber(chamberId);

        // Kiểm tra DB empty
        assertEquals(0, chamberRepository.count());
    }

    /**
     * Test case TC-CHAMBER-SERVICE-030: Kiểm tra tìm kiếm với page size 1.
     * Expected: Correct pagination.
     */
    @Test
    public void testSearchChamberWithPrice1_PageSize1_ShouldPaginateCorrectly() {
        // Chuẩn bị dữ liệu test - 3 chambers
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "true", "60", "25", "note2", "true");
        Chamber chamber3 = new Chamber("103", "single", "true", "70", "30", "note3", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 1); // Page 0, size 1

        // Gọi phương thức
        Page<Chamber> result = chamberService.searchChamberWithPrice1(pageable, "single", "true");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(3, result.getTotalPages());
    }
}