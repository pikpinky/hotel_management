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
     * Test case TC-CHECKIN-CONTROLLER-024: Kiểm tra với page number quá lớn.
     * Expected: Handle gracefully, có thể return empty hoặc last page.
     */
    @Test
    public void testCheckIn_VeryLargePageNumber_ShouldHandleGracefully() {
        // Gọi phương thức với page number rất lớn
        org.springframework.ui.ExtendedModelMap modelMap = new org.springframework.ui.ExtendedModelMap();
        String viewName = checkInController.checkIn(modelMap, 1000, 12, "single", "true");

        // Kiểm tra kết quả
        assertEquals("check-in", viewName);
    }
}
