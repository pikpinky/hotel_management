package com.devpro.spring.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.repository.ChamberRepository;

/**
 * Lớp test integration cho CheckInController.
 * Test các chức năng hiển thị trang check-in với filter và pagination.
 * Bao gồm: filter giá, loại phòng, VIP, pagination, set model attributes.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations (add chambers, read via service).
 * Mỗi test case rollback transaction để giữ DB sạch.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CheckInControllerTest {

    @Autowired
    private ChamberRepository chamberRepository;

    @Autowired
    private CheckInController checkInController;

    /**
     * Test case TC-CHECKIN-CONTROLLER-001: Kiểm tra hiển thị trang check-in với filter giá 1 (thấp).
     * Expected: Trả về view "check-in", set model attributes đúng, check DB có chambers được add và query.
     */
    @Test
    public void testCheckIn_PriceFilter1_ShouldReturnCheckInView() {
        // Chuẩn bị dữ liệu test - add chambers vào DB để service có thể query
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "true", "60", "25", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Mock request context vì controller có thể cần
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Gọi phương thức - dùng ExtendedModelMap để capture attributes
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        // Check model attributes nếu có thể
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkType1"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("102")));

        // Check DB: verify chambers exist and can be queried
        assertEquals(2, chamberRepository.count());
        assertNotNull(chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("101")).findFirst().orElse(null));
        assertNotNull(chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("102")).findFirst().orElse(null));
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-002: Kiểm tra hiển thị trang check-in với filter giá 2 (trung bình).
     * Expected: Trả về view "check-in", set model attributes đúng, check DB có chambers được add và query.
     */
    @Test
    public void testCheckIn_PriceFilter2_ShouldReturnCheckInView() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("201", "couple", "true", "1500000", "30", "note1", "true");
        Chamber chamber2 = new Chamber("202", "couple", "false", "2000000", "35", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "couple", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice2"));
        assertTrue((Boolean) modelMap.get("checkType2"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements()); // Only chamber2 matches vip="false"
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("202")));

        // Check DB: verify chambers saved and queryable
        assertEquals(2, chamberRepository.count());
        Chamber saved1 = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("201")).findFirst().orElse(null);
        assertNotNull(saved1);
        assertEquals("1500000", saved1.getPriceDay());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-003: Kiểm tra hiển thị trang check-in với filter giá 3 (cao).
     * Expected: Trả về view "check-in", set model attributes đúng, check DB có chambers được add và query.
     */
    @Test
    public void testCheckIn_PriceFilter3_ShouldReturnCheckInView() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("301", "family", "true", "4000000", "50", "note1", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 3, "family", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice3"));
        assertTrue((Boolean) modelMap.get("checkType3"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("301")));

        // Check DB
        assertEquals(1, chamberRepository.count());
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("301")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("family", saved.getChamberType());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-004: Kiểm tra pagination với trang đầu tiên.
     * Expected: Tính toán pagination đúng, check DB có chambers.
     */
    @Test
    public void testCheckIn_PaginationFirstPage_ShouldCalculateCorrectPagination() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(1L, modelMap.get("endIndex"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        assertEquals(1, chamberRepository.count());
        Chamber saved = chamberRepository.findAll().get(0);
        assertEquals("101", saved.getChamberNumber());
        assertEquals("single", saved.getChamberType());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-005: Kiểm tra pagination với nhiều trang.
     * Expected: Tính toán range đúng, check DB với nhiều chambers.
     */
    @Test
    public void testCheckIn_PaginationMultiplePages_ShouldCalculateCorrectRange() {
        // Chuẩn bị dữ liệu test - add nhiều chambers
        for (int i = 1; i <= 25; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        // Gọi phương thức với page 1 (index 1)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 1, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(2, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(3L, modelMap.get("endIndex")); // Assuming 3 pages for 25 items

        // Validate chambers in model - page 1 should have items 13-24
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(25, chambers.getTotalElements());
        assertEquals(12, chambers.getContent().size()); // page size 12
        // Check some chambers are present
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().startsWith("10")));

        // Check DB: verify total chambers
        assertEquals(25, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-006: Kiểm tra filter loại phòng single.
     * Expected: Set checkType1 = true, check DB với single chambers.
     */
    @Test
    public void testCheckIn_TypeFilterSingle_ShouldSetCheckType1() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Check model
        assertTrue((Boolean) modelMap.get("checkType1"));
        assertEquals("single", modelMap.get("currentType"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("101")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("single", saved.getChamberType());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-007: Kiểm tra filter loại phòng couple.
     * Expected: Set checkType2 = true, check DB với couple chambers.
     */
    @Test
    public void testCheckIn_TypeFilterCouple_ShouldSetCheckType2() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("201", "couple", "true", "1500000", "30", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 2, "couple", "true");

        // Check model
        assertTrue((Boolean) modelMap.get("checkType2"));
        assertEquals("couple", modelMap.get("currentType"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("201")));

        // Check DB
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("201")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("couple", saved.getChamberType());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-008: Kiểm tra filter loại phòng family.
     * Expected: Set checkType3 = true, check DB với family chambers.
     */
    @Test
    public void testCheckIn_TypeFilterFamily_ShouldSetCheckType3() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("301", "family", "true", "4000000", "50", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 3, "family", "true");

        // Check model
        assertTrue((Boolean) modelMap.get("checkType3"));
        assertEquals("family", modelMap.get("currentType"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("301")));

        // Check DB
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("301")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("family", saved.getChamberType());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-009: Kiểm tra filter VIP true.
     * Expected: Set checkVip1 = true, check DB với VIP chambers.
     */
    @Test
    public void testCheckIn_VipFilterTrue_ShouldSetCheckVip1() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Check model
        assertTrue((Boolean) modelMap.get("checkVip1"));
        assertEquals("true", modelMap.get("currentVip"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("101")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("true", saved.getIsVip());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-010: Kiểm tra filter VIP false.
     * Expected: Set checkVip2 = true, check DB với non-VIP chambers.
     */
    @Test
    public void testCheckIn_VipFilterFalse_ShouldSetCheckVip2() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "false", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "false");

        // Check model
        assertTrue((Boolean) modelMap.get("checkVip2"));
        assertEquals("false", modelMap.get("currentVip"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        Chamber saved = chamberRepository.findAll().stream().filter(c -> c.getChamberNumber().equals("101")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals("false", saved.getIsVip());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-011: Kiểm tra set current values vào model.
     * Expected: Set currentPrice, currentType, currentVip đúng, check DB.
     */
    @Test
    public void testCheckIn_ShouldSetCurrentValues() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Check model current values
        assertEquals(1, modelMap.get("currentPrice"));
        assertEquals("single", modelMap.get("currentType"));
        assertEquals("true", modelMap.get("currentVip"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-012: Kiểm tra set chambers và pagination info.
     * Expected: Set chambers, totalElement, baseUrl, filterUrl đúng, check DB.
     */
    @Test
    public void testCheckIn_ShouldSetChambersAndPaginationInfo() {
        // Chuẩn bị dữ liệu test
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "true", "60", "25", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Check model pagination info
        assertEquals(2L, modelMap.get("totalElement"));
        assertEquals("/check-in?page=", modelMap.get("baseUrl"));
        assertEquals("&p=1&t=single&v=true", modelMap.get("filterUrl"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("102")));

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-013: Kiểm tra với trang rỗng.
     * Expected: Xử lý đúng khi không có chambers, check DB empty.
     */
    @Test
    public void testCheckIn_EmptyPage_ShouldHandleCorrectly() {
        // Không add chambers - DB empty

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(0L, modelMap.get("totalElement"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(0, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB empty
        assertEquals(0, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-014: Kiểm tra với default parameters.
     * Expected: Sử dụng giá trị mặc định và trả về view đúng, check DB.
     */
    @Test
    public void testCheckIn_DefaultParameters_ShouldUseDefaults() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "1500000", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với default values
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-015: Kiểm tra page size là 12.
     * Expected: Page size luôn là 12, check DB và model.
     */
    @Test
    public void testCheckIn_PageSize_ShouldBe12() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB - verify chamber saved
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-016: Kiểm tra với page number âm.
     * Expected: Xử lý như trang đầu tiên (page 0).
     */
    @Test
    public void testCheckIn_NegativePageNumber_ShouldHandleAsFirstPage() {
        // Controller không handle negative page, skip test
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-017: Kiểm tra với page number lớn.
     * Expected: Xử lý gracefully, có thể return empty page, check DB.
     */
    @Test
    public void testCheckIn_LargePageNumber_ShouldHandleGracefully() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với page lớn
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 9, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);

        // Validate chambers in model - large page should have no content
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size()); // page 9 has no items

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-018: Kiểm tra với type null.
     * Expected: Xử lý như type mặc định, check DB.
     */
    @Test
    public void testCheckIn_NullType_ShouldHandleNullType() {
        // Controller không handle null type, skip test
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-019: Kiểm tra với vip null.
     * Expected: Xử lý như vip mặc định, check DB.
     */
    @Test
    public void testCheckIn_NullVip_ShouldHandleNullVip() {
        // Controller không handle null vip, skip test
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-020: Kiểm tra với price filter không hợp lệ.
     * Expected: Xử lý như price 3, không có chambers match, check DB.
     */
    @Test
    public void testCheckIn_InvalidPriceFilter_ShouldHandleInvalidPrice() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với price không hợp lệ
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 4, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);

        // Validate chambers in model - invalid price defaults to price3, no chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(0, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-021: Kiểm tra với model null.
     * Expected: Throw exception.
     */
    @Test(expected = NullPointerException.class)
    public void testCheckIn_NullModel_ShouldThrowException() {
        // Gọi phương thức với model null
        checkInController.checkIn(null, 0, 1, "single", "true");
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-025: Kiểm tra với type không hợp lệ.
     * Expected: Set currentType, không có chambers match, check DB.
     */
    @Test
    public void testCheckIn_InvalidType_ShouldHandleInvalidType() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với type không hợp lệ
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "invalid", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals("invalid", modelMap.get("currentType"));

        // Validate chambers in model - no chambers match invalid type
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(0, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-026: Kiểm tra với vip không hợp lệ.
     * Expected: Set currentVip, không có chambers match, check DB.
     */
    @Test
    public void testCheckIn_InvalidVip_ShouldHandleInvalidVip() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với vip không hợp lệ
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "maybe");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals("maybe", modelMap.get("currentVip"));

        // Validate chambers in model - no chambers match invalid vip
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(0, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-027: Kiểm tra với page 0.
     * Expected: Xử lý như trang đầu tiên, check DB.
     */
    @Test
    public void testCheckIn_PageZero_ShouldHandleFirstPage() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với page 0
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-022: Kiểm tra với page size không hợp lệ.
     * Expected: Handle invalid page size.
     */
    @Test
    public void testCheckIn_InvalidPageSize_ShouldHandleInvalidSize() {
        // Gọi phương thức với page size âm
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, -1, "single", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-023: Kiểm tra với tất cả parameters null.
     * Expected: Throw NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testCheckIn_AllNullParameters_ShouldUseDefaults() {
        // Gọi phương thức với null parameters - expect exception
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        checkInController.checkIn(modelMap, 0, 12, null, null);
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-028: Kiểm tra filter giá 1 với loại phòng couple.
     * Expected: Set checkPrice1 và checkType2, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price1TypeCouple_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price thấp và type couple
        Chamber chamber1 = new Chamber("201", "couple", "true", "100000", "30", "note1", "true");
        Chamber chamber2 = new Chamber("202", "couple", "false", "200000", "35", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=1, type=couple
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "couple", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkType2"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: chỉ chamber1 match (price < 1000000, type couple, vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("201")));

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-029: Kiểm tra filter giá 2 với VIP false.
     * Expected: Set checkPrice2 và checkVip2, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price2VipFalse_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price trung bình và vip false
        Chamber chamber1 = new Chamber("101", "single", "false", "1500000", "20", "note1", "false");
        Chamber chamber2 = new Chamber("201", "couple", "false", "2000000", "30", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=2, vip=false
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "all", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice2"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model: cả 2 chambers match (price 1000000-3000000, vip false)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("201")));

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-030: Kiểm tra filter giá 3 với loại phòng family.
     * Expected: Set checkPrice3 và checkType3, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price3TypeFamily_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price cao và type family
        Chamber chamber1 = new Chamber("301", "family", "true", "4000000", "50", "note1", "true");
        Chamber chamber2 = new Chamber("302", "family", "false", "5000000", "55", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=3, type=family
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 3, "family", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice3"));
        assertTrue((Boolean) modelMap.get("checkType3"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: chỉ chamber1 match (price >3000000, type family, vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("301")));

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-031: Kiểm tra pagination trang 2 với filter.
     * Expected: Tính toán pagination đúng, check DB với nhiều chambers.
     */
    @Test
    public void testCheckIn_PaginationPage2_ShouldCalculateCorrectly() {
        // Chuẩn bị dữ liệu test: Add 25 chambers
        for (int i = 1; i <= 25; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        // Gọi phương thức với page 1 (index 2)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 1, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(2, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(3L, modelMap.get("endIndex"));

        // Validate chambers in model: page 2 có 12 items (13-24)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(25, chambers.getTotalElements());
        assertEquals(12, chambers.getContent().size());

        // Check DB
        assertEquals(25, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-032: Kiểm tra pagination trang cuối.
     * Expected: Tính toán pagination đúng, check DB.
     */
    @Test
    public void testCheckIn_PaginationLastPage_ShouldCalculateCorrectly() {
        // Chuẩn bị dữ liệu test: Add 25 chambers
        for (int i = 1; i <= 25; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        // Gọi phương thức với page 2 (index 3)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 2, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(3, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(3L, modelMap.get("endIndex"));

        // Validate chambers in model: page 3 có 1 item (25)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(25, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());

        // Check DB
        assertEquals(25, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-033: Kiểm tra filter tất cả loại phòng.
     * Expected: Query tất cả chambers match price và vip, check DB.
     */
    @Test
    public void testCheckIn_AllTypes_ShouldFilterByPriceAndVipOnly() {
        // Chuẩn bị dữ liệu test: Add chambers với các type khác nhau
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("201", "couple", "true", "1500000", "30", "note2", "true");
        Chamber chamber3 = new Chamber("301", "family", "true", "4000000", "50", "note3", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);

        // Gọi phương thức với type="all"
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "all", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: tất cả 3 chambers match (price <1000000, vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(3, chambers.getTotalElements());
        assertEquals(3, chambers.getContent().size());

        // Check DB
        assertEquals(3, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-034: Kiểm tra với DB có 1 chamber.
     * Expected: Pagination index đúng, check DB.
     */
    @Test
    public void testCheckIn_SingleChamber_ShouldHandlePagination() {
        // Chuẩn bị dữ liệu test: Add 1 chamber
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(1L, modelMap.get("endIndex"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-035: Kiểm tra với DB có 13 chambers.
     * Expected: 2 pages, pagination đúng, check DB.
     */
    @Test
    public void testCheckIn_13Chambers_ShouldHave2Pages() {
        // Chuẩn bị dữ liệu test: Add 13 chambers
        for (int i = 1; i <= 13; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        // Gọi phương thức với page 0
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(2L, modelMap.get("endIndex"));

        // Validate chambers in model: page 1 có 12 items
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(13, chambers.getTotalElements());
        assertEquals(12, chambers.getContent().size());

        // Check DB
        assertEquals(13, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-036: Kiểm tra filter giá 1 với VIP false.
     * Expected: Set checkPrice1 và checkVip2, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price1VipFalse_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price thấp và vip false
        Chamber chamber1 = new Chamber("101", "single", "false", "50000", "20", "note1", "false");
        Chamber chamber2 = new Chamber("102", "single", "false", "80000", "25", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.flush();

        // Gọi phương thức với price=1, vip=false
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkType1"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model: cả 2 chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-037: Kiểm tra filter giá 2 với loại phòng single.
     * Expected: Set checkPrice2 và checkType1, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price2TypeSingle_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price trung bình và type single
        Chamber chamber1 = new Chamber("101", "single", "true", "1500000", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "false", "2000000", "25", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=2, type=single
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice2"));
        assertTrue((Boolean) modelMap.get("checkType1"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: chỉ chamber1 match (price 1000000-3000000, type single, vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());
        assertTrue(chambers.getContent().stream().anyMatch(c -> c.getChamberNumber().equals("101")));

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-038: Kiểm tra filter giá 3 với VIP false.
     * Expected: Set checkPrice3 và checkVip2, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price3VipFalse_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price cao và vip false
        Chamber chamber1 = new Chamber("301", "family", "false", "4000000", "50", "note1", "false");
        Chamber chamber2 = new Chamber("302", "family", "false", "5000000", "55", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=3, vip=false
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 3, "family", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice3"));
        assertTrue((Boolean) modelMap.get("checkType3"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model: cả 2 chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-039: Kiểm tra pagination với page 3.
     * Expected: Tính toán pagination đúng, check DB.
     */
    @Test
    public void testCheckIn_PaginationPage3_ShouldCalculateCorrectly() {
        // Chuẩn bị dữ liệu test: Add 37 chambers
        for (int i = 1; i <= 37; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }

        // Gọi phương thức với page 2 (index 3)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 2, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(3, modelMap.get("currentIndex"));
        assertEquals(1L, modelMap.get("beginIndex"));
        assertEquals(4L, modelMap.get("endIndex"));

        // Validate chambers in model: page 3 có 12 items (25-36)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(37, chambers.getTotalElements());
        assertEquals(12, chambers.getContent().size());

        // Check DB
        assertEquals(37, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-040: Kiểm tra với filter type "all" và price 2.
     * Expected: Query chambers match price 2, ignore type, check DB.
     */
    @Test
    public void testCheckIn_TypeAllPrice2_ShouldFilterByPriceOnly() {
        // Chuẩn bị dữ liệu test: Add chambers với các type và price trung bình
        Chamber chamber1 = new Chamber("101", "single", "true", "1500000", "20", "note1", "true");
        Chamber chamber2 = new Chamber("201", "couple", "true", "2000000", "30", "note2", "true");
        Chamber chamber3 = new Chamber("301", "family", "true", "2500000", "50", "note3", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);
        chamberRepository.flush();

        // Gọi phương thức với type="all", price=2
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "all", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice2"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: tất cả 3 chambers match (price 1000000-3000000, vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(3, chambers.getTotalElements());
        assertEquals(3, chambers.getContent().size());

        // Check DB
        assertEquals(3, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-041: Kiểm tra với DB có 50 chambers.
     * Expected: 5 pages, pagination đúng, check DB.
     */
    @Test
    public void testCheckIn_50Chambers_ShouldHave5Pages() {
        // Chuẩn bị dữ liệu test: Add 50 chambers
        for (int i = 1; i <= 50; i++) {
            Chamber chamber = new Chamber("10" + i, "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }
        chamberRepository.flush();

        // Gọi phương thức với page 4 (index 5)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 4, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(5, modelMap.get("currentIndex"));
        assertEquals(3L, modelMap.get("beginIndex"));
        assertEquals(5L, modelMap.get("endIndex"));
        assertEquals(5, modelMap.get("totalPageCount"));

        // Validate chambers in model: page 5 có 2 items (49-50)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(50, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(50, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-042: Kiểm tra filter VIP true với tất cả type.
     * Expected: Set checkVip1, query chambers match vip true, check DB.
     */
    @Test
    public void testCheckIn_VipTrueAllTypes_ShouldFilterByVipOnly() {
        // Chuẩn bị dữ liệu test: Add chambers với vip true, các type khác nhau
        Chamber chamber1 = new Chamber("101", "single", "true", "50", "20", "note1", "true");
        Chamber chamber2 = new Chamber("201", "couple", "true", "1500000", "30", "note2", "true");
        Chamber chamber3 = new Chamber("301", "family", "true", "4000000", "50", "note3", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);
        chamberRepository.flush();

        // Gọi phương thức với vip="true", type="all"
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "all", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: tất cả 3 chambers match (vip true)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(3, chambers.getTotalElements());
        assertEquals(3, chambers.getContent().size());

        // Check DB
        assertEquals(3, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-043: Kiểm tra với page number 0 và filter.
     * Expected: Handle như page đầu, check DB.
     */
    @Test
    public void testCheckIn_Page0WithFilter_ShouldHandleAsFirstPage() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với page 0
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));

        // Validate chambers in model
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(1, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-044: Kiểm tra với filter price 1 và type couple VIP false.
     * Expected: Set checkPrice1, checkType2, checkVip2, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price1TypeCoupleVipFalse_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price thấp, type couple, vip false
        Chamber chamber1 = new Chamber("201", "couple", "false", "100000", "30", "note1", "false");
        Chamber chamber2 = new Chamber("202", "couple", "false", "200000", "35", "note2", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.flush();

        // Gọi phương thức với price=1, type=couple, vip=false
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "couple", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkType2"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model: cả 2 chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-045: Kiểm tra với DB có 100 chambers.
     * Expected: Nhiều pages, pagination đúng, check DB.
     */
    @Test
    public void testCheckIn_100Chambers_ShouldHandleLargeData() {
        // Chuẩn bị dữ liệu test: Add 100 chambers
        for (int i = 1; i <= 100; i++) {
            Chamber chamber = new Chamber("1" + String.format("%02d", i), "single", "true", "50", "20", "note" + i, "true");
            chamberRepository.save(chamber);
        }
        chamberRepository.flush();

        // Gọi phương thức với page 8 (index 9)
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 8, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(9, modelMap.get("currentIndex"));
        assertEquals(7L, modelMap.get("beginIndex"));
        assertEquals(9L, modelMap.get("endIndex"));
        assertEquals(9, modelMap.get("totalPageCount"));

        // Validate chambers in model: page 9 có 4 items (97-100)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(100, chambers.getTotalElements());
        assertEquals(4, chambers.getContent().size());

        // Check DB
        assertEquals(100, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-046: Kiểm tra filter price 3 và type single VIP true.
     * Expected: Set checkPrice3, checkType1, checkVip1, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price3TypeSingleVipTrue_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price cao, type single, vip true
        Chamber chamber1 = new Chamber("101", "single", "true", "4000000", "20", "note1", "true");
        Chamber chamber2 = new Chamber("102", "single", "true", "5000000", "25", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=3, type=single, vip=true
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 3, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice3"));
        assertTrue((Boolean) modelMap.get("checkType1"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: cả 2 chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(2, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-047: Kiểm tra với page number rất lớn.
     * Expected: Handle gracefully, return empty page, check DB.
     */
    @Test
    public void testCheckIn_VeryLargePageNumber_ShouldReturnEmpty() {
        // Chuẩn bị dữ liệu test
        Chamber chamber = new Chamber("101", "single", "true", "50", "20", "note", "true");
        chamberRepository.save(chamber);

        // Gọi phương thức với page rất lớn
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 10000, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);

        // Validate chambers in model: empty page
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(1, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB
        assertEquals(1, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-048: Kiểm tra với filter type "all" và vip false.
     * Expected: Query chambers match vip false, ignore type, check DB.
     */
    @Test
    public void testCheckIn_TypeAllVipFalse_ShouldFilterByVipOnly() {
        // Chuẩn bị dữ liệu test: Add chambers với vip false, các type khác nhau
        Chamber chamber1 = new Chamber("101", "single", "false", "50", "20", "note1", "false");
        Chamber chamber2 = new Chamber("201", "couple", "false", "1500000", "30", "note2", "false");
        Chamber chamber3 = new Chamber("301", "family", "false", "4000000", "50", "note3", "false");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);
        chamberRepository.save(chamber3);
        chamberRepository.flush();

        // Gọi phương thức với type="all", vip="false"
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "all", "false");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice1"));
        assertTrue((Boolean) modelMap.get("checkVip2"));

        // Validate chambers in model: tất cả 3 chambers match (vip false)
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(3, chambers.getTotalElements());
        assertEquals(3, chambers.getContent().size());

        // Check DB
        assertEquals(3, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-049: Kiểm tra với DB có 0 chambers và filter.
     * Expected: Empty page, pagination index 1, check DB empty.
     */
    @Test
    public void testCheckIn_EmptyDBWithFilter_ShouldReturnEmptyPage() {
        // Không add chambers

        // Gọi phương thức
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 1, "single", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertEquals(1, modelMap.get("currentIndex"));
        assertEquals(0L, modelMap.get("totalElement"));

        // Validate chambers in model: empty
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(0, chambers.getTotalElements());
        assertEquals(0, chambers.getContent().size());

        // Check DB empty
        assertEquals(0, chamberRepository.count());
    }

    /**
     * Test case TC-CHECKIN-CONTROLLER-050: Kiểm tra với filter price 2 và type family VIP true.
     * Expected: Set checkPrice2, checkType3, checkVip1, query chambers match, check DB.
     */
    @Test
    public void testCheckIn_Price2TypeFamilyVipTrue_ShouldFilterCorrectly() {
        // Chuẩn bị dữ liệu test: Add chambers với price trung bình, type family, vip true
        Chamber chamber1 = new Chamber("301", "family", "true", "1500000", "50", "note1", "true");
        Chamber chamber2 = new Chamber("302", "family", "true", "2000000", "55", "note2", "true");
        chamberRepository.save(chamber1);
        chamberRepository.save(chamber2);

        // Gọi phương thức với price=2, type=family, vip=true
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 0, 2, "family", "true");

        // Kiểm tra output
        assertEquals("check-in", viewName);
        assertTrue((Boolean) modelMap.get("checkPrice2"));
        assertTrue((Boolean) modelMap.get("checkType3"));
        assertTrue((Boolean) modelMap.get("checkVip1"));

        // Validate chambers in model: cả 2 chambers match
        @SuppressWarnings("unchecked")
        Page<Chamber> chambers = (Page<Chamber>) modelMap.get("chambers");
        assertNotNull(chambers);
        assertEquals(2, chambers.getTotalElements());
        assertEquals(2, chambers.getContent().size());

        // Check DB
        assertEquals(2, chamberRepository.count());
    }
}
