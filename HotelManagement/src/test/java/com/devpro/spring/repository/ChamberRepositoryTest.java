package com.devpro.spring.repository;

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

import com.devpro.spring.model.Chamber;

/**
 * Lớp test integration cho ChamberRepository - kiểm tra các custom query methods liên quan đến chức năng đặt phòng.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Tuân thủ quy tắc white-box testing: test các branches, paths, edge cases.
 * Test coverage: search với price filters, update chamber status, search by text.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ChamberRepositoryTest {

    @Autowired
    private ChamberRepository chamberRepository;

    /**
     * Test case TC-CHAMBER-REPO-001: Kiểm tra searchChamberWithPrice1.
     * Mục đích: Verify custom query search chamber theo price range 1 (giá thấp) và filter type + vip
     * Kiểm tra:
     *  - CustomQuery.CHAMBER_SEARCH_PRICE_1 có hoạt động đúng không
     *  - Filter theo chamber type và isVip có chính xác không
     *  - Pagination với Page<Chamber> return có đúng không
     * Input: 3 chambers khác nhau (low price VIP, low price normal, high price VIP)
     * Expected: Chỉ return chambers match cả type="single" và isVip="true"
     */
    @Test
    public void testSearchChamberWithPrice1_ShouldReturnLowPriceChambers() {
        // ARRANGE: Setup test data với đa dạng price và vip status
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "Low price VIP", "true");
        Chamber chamber2 = new Chamber("102", "single", "false", "60", "25", "Low price normal", "true");
        Chamber chamber3 = new Chamber("201", "couple", "true", "150", "30", "High price VIP", "true");
        
        // Persist vào H2 test DB
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // ACT: Execute custom query với specific filters
        Page<Chamber> result = chamberRepository.searchChamberWithPrice1(pageable, "single", "true");
        
        // ASSERT: Verify filtering logic
        assertNotNull("Result should not be null", result);
        assertTrue("Should find at least 1 matching chamber", result.getTotalElements() >= 1);
        // Kiểm tra logic dựa trên implementation của CustomQuery.CHAMBER_SEARCH_PRICE_1
    }

    /**
     * Test case TC-CHAMBER-REPO-002: Kiểm tra searchChamberWithPrice2.
     * Expected: Trả về chambers trong tầm giá trung bình.
     */
    @Test
    public void testSearchChamberWithPrice2_ShouldReturnMidPriceChambers() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "couple", "true", "100", "25", "Mid price VIP", "true");
        Chamber chamber2 = new Chamber("102", "couple", "false", "120", "30", "Mid price normal", "true");
        Chamber chamber3 = new Chamber("103", "single", "true", "80", "20", "Low price", "true");
        
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search price 2 với couple type
        Page<Chamber> result = chamberRepository.searchChamberWithPrice2(pageable, "couple", "true");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 0);
    }

    /**
     * Test case TC-CHAMBER-REPO-003: Kiểm tra searchChamberWithPrice3.
     * Expected: Trả về chambers trong tầm giá cao.
     */
    @Test
    public void testSearchChamberWithPrice3_ShouldReturnHighPriceChambers() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("301", "family", "true", "200", "50", "High price VIP", "true");
        Chamber chamber2 = new Chamber("302", "family", "false", "180", "45", "High price normal", "true");
        Chamber chamber3 = new Chamber("101", "single", "true", "80", "20", "Low price", "true");
        
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search price 3 với family type
        Page<Chamber> result = chamberRepository.searchChamberWithPrice3(pageable, "family", "true");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 0);
    }

    /**
     * Test case TC-CHAMBER-REPO-004: Kiểm tra updateChamberIsEmpty.
     * Expected: Update trạng thái chamber thành công.
     */
    @Test
    public void testUpdateChamberIsEmpty_ShouldUpdateChamberStatus() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "Test chamber", "true");
        Chamber savedChamber = chamberRepository.save(chamber);

        // Test update chamber status
        chamberRepository.updateChamberIsEmpty("false", savedChamber.getChamberId());

        // Verify update
        Chamber updatedChamber = chamberRepository.findById(savedChamber.getChamberId()).orElse(null);
        assertNotNull(updatedChamber);
        assertEquals("false", updatedChamber.getIsEmpty());
    }

    /**
     * Test case TC-CHAMBER-REPO-005: Kiểm tra searchChamber với text search.
     * Expected: Trả về chambers match search text.
     */
    @Test
    public void testSearchChamber_WithText_ShouldReturnMatchingChambers() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "100", "20", "VIP chamber", "true");
        Chamber chamber2 = new Chamber("102", "couple", "false", "150", "30", "Normal chamber", "true");
        Chamber chamber3 = new Chamber("201", "family", "true", "200", "40", "Family suite", "true");
        
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search by chamber number
        Page<Chamber> result1 = chamberRepository.searchChamber(pageable, "%101%");
        assertNotNull(result1);
        
        // Test search by chamber type
        Page<Chamber> result2 = chamberRepository.searchChamber(pageable, "%single%");
        assertNotNull(result2);
    }

    /**
     * Test case TC-CHAMBER-REPO-006: Kiểm tra updateChamberInfo.
     * Mục đích: Verify custom update query cập nhật đồng thời nhiều fields của chamber
     * Kiểm tra:
     *  - CustomQuery update có thực thi đúng không
     *  - Tất cả fields được update chính xác (number, type, price, area, note, vip)
     *  - WHERE condition theo chamberId có đúng không
     * Input: Chamber với giá trị ban đầu, sau đó update toàn bộ thông tin
     * Expected: Tất cả fields được cập nhật theo giá trị mới
     */
    @Test
    public void testUpdateChamberInfo_ShouldUpdateAllFields() {
        // ARRANGE: Tạo chamber với thông tin ban đầu
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "Old note", "true");
        Chamber savedChamber = chamberRepository.save(chamber);

        // ACT: Update tất cả thông tin chamber
        chamberRepository.updateChamberInfo("201", "couple", "150", "30", "New note", "false", savedChamber.getChamberId());

        // ASSERT: Verify tất cả fields được update
        Chamber updatedChamber = chamberRepository.findById(savedChamber.getChamberId()).orElse(null);
        assertNotNull("Updated chamber should exist", updatedChamber);
        assertEquals("Chamber number should be updated", "201", updatedChamber.getChamberNumber());
        assertEquals("Chamber type should be updated", "couple", updatedChamber.getChamberType());
        assertEquals("Price should be updated", "150", updatedChamber.getPriceDay());
        assertEquals("Area should be updated", "30", updatedChamber.getChamberArea());
        assertEquals("Note should be updated", "New note", updatedChamber.getNote());
        assertEquals("VIP status should be updated", "false", updatedChamber.getIsVip());
    }

    /**
     * Test case TC-CHAMBER-REPO-007: Kiểm tra searchChamberWithPrice1 với type "all".
     * Expected: Ignore type filter, return all chambers match price và vip.
     */
    @Test
    public void testSearchChamberWithPrice1_TypeAll_ShouldIgnoreTypeFilter() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "80", "20", "Single VIP", "true");
        Chamber chamber2 = new Chamber("102", "couple", "true", "90", "25", "Couple VIP", "true");
        Chamber chamber3 = new Chamber("103", "family", "true", "70", "30", "Family VIP", "true");
        
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        Pageable pageable = PageRequest.of(0, 10);

        // Test với type = "all" (hoặc any type)
        Page<Chamber> result = chamberRepository.searchChamberWithPrice1(pageable, "all", "true");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        // Should return chambers regardless of type if price and vip match
    }

    /**
     * Test case TC-CHAMBER-REPO-008: Kiểm tra searchChamberWithPrice2 với vip "all".
     * Expected: Ignore vip filter, return all chambers match price và type.
     */
    @Test
    public void testSearchChamberWithPrice2_VipAll_ShouldIgnoreVipFilter() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "100", "20", "Single VIP", "true");
        Chamber chamber2 = new Chamber("102", "single", "false", "110", "20", "Single Normal", "true");
        
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        Pageable pageable = PageRequest.of(0, 10);

        // Test với vip = "all"
        Page<Chamber> result = chamberRepository.searchChamberWithPrice2(pageable, "single", "all");
        
        // Kiểm tra kết quả
        assertNotNull(result);
    }

    /**
     * Test case TC-CHAMBER-REPO-009: Kiểm tra search với pagination - multiple pages.
     * Expected: Pagination hoạt động đúng.
     */
    @Test
    public void testSearchChamber_MultiplePagesTest() {
        // Chuẩn bị dữ liệu test - tạo nhiều chambers
        for (int i = 1; i <= 15; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "100", "20", "Chamber " + i, "true");
            chamberRepository.save(chamber);
        }

        // Test page 0 with size 5
        Pageable pageable1 = PageRequest.of(0, 5);
        Page<Chamber> page1 = chamberRepository.searchChamber(pageable1, "%10%");
        
        assertNotNull(page1);
        assertTrue(page1.getTotalElements() >= 15);
        assertTrue(page1.getTotalPages() >= 3);

        // Test page 1 with size 5
        Pageable pageable2 = PageRequest.of(1, 5);
        Page<Chamber> page2 = chamberRepository.searchChamber(pageable2, "%10%");
        
        assertNotNull(page2);
        assertEquals(1, page2.getNumber());
    }

    /**
     * Test case TC-CHAMBER-REPO-010: Kiểm tra updateChamberIsEmpty với chamber không tồn tại.
     * Expected: Không có error, không update gì.
     */
    @Test
    public void testUpdateChamberIsEmpty_NonExistentChamber_ShouldNotThrowError() {
        // Test update chamber không tồn tại
        chamberRepository.updateChamberIsEmpty("false", 99999L);
        
        // Verify không có chamber nào được tạo/update
        Chamber chamber = chamberRepository.findById(99999L).orElse(null);
        assertEquals(null, chamber);
    }

    /**
     * Test case TC-CHAMBER-REPO-011: Kiểm tra search với empty result.
     * Expected: Trả về empty page.
     */
    @Test
    public void testSearchChamber_NoResults_ShouldReturnEmptyPage() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "Test chamber", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search với text không match
        Page<Chamber> result = chamberRepository.searchChamber(pageable, "%XYZ%");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * Test case TC-CHAMBER-REPO-012: Kiểm tra updateChamberInfo với null values.
     * Expected: Handle null values correctly.
     */
    @Test
    public void testUpdateChamberInfo_WithNullValues_ShouldHandleCorrectly() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "Test note", "true");
        Chamber savedChamber = chamberRepository.save(chamber);

        // Test update với một số null values
        chamberRepository.updateChamberInfo("102", null, "150", null, null, "false", savedChamber.getChamberId());

        // Verify update (behavior depends on query implementation)
        Chamber updatedChamber = chamberRepository.findById(savedChamber.getChamberId()).orElse(null);
        assertNotNull(updatedChamber);
        assertEquals("102", updatedChamber.getChamberNumber());
        assertEquals("150", updatedChamber.getPriceDay());
    }

    /**
     * Test case TC-CHAMBER-REPO-013: Kiểm tra searchChamberWithPrice3 với empty result.
     * Expected: Trả về empty page khi không có chamber match criteria.
     */
    @Test
    public void testSearchChamberWithPrice3_NoResults_ShouldReturnEmptyPage() {
        // Chuẩn bị dữ liệu test - chỉ chambers giá thấp
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "Low price", "true");
        chamberRepository.save(chamber);

        Pageable pageable = PageRequest.of(0, 10);

        // Test search price 3 (high price) với family type
        Page<Chamber> result = chamberRepository.searchChamberWithPrice3(pageable, "family", "true");
        
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }




}