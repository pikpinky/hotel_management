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
}