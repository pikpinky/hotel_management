package com.devpro.spring.service;

import java.util.List;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.dto.FoodItemDto;
import com.devpro.spring.model.Category;
import com.devpro.spring.model.FoodItem;
import com.devpro.spring.repository.CategoryRepository;
import com.devpro.spring.repository.FoodItemRepository;

/**
 * Lớp test integration cho FoodItemService.
 * Test các chức năng quản lý món ăn trong hệ thống quản lý khách sạn.
 * Bao gồm: validation dữ liệu theo logic nghiệp vụ thực tế, CRUD operations, tìm kiếm và phân trang.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ thực tế của khách sạn: tên món phải duy nhất, giá phải > 0, category bắt buộc, etc.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FoodItemServiceTest {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    /**
     * Test case TC-FOODITEM-SERVICE-001: Kiểm tra saveFoodItem với dữ liệu hợp lệ.
     * Expected: Món ăn được lưu thành công vào database.
     */
    @Test
    public void testSaveFoodItem_ValidData_ShouldSaveSuccessfully() {
        // Chuẩn bị dữ liệu test: Tạo category hợp lệ trước
        Category category = createValidCategory();
        categoryRepository.save(category);
        em.flush();

        // Tạo FoodItem với dữ liệu hợp lệ theo nghiệp vụ khách sạn
        FoodItem foodItem = new FoodItem("Phở Bò", "Phở bò truyền thống", "50000", "pho-bo.jpg", category);

        long initialCount = foodItemRepository.count();

        // Gọi service method
        foodItemService.saveFoodItem(foodItem);

        // Kiểm tra kết quả: FoodItem đã được lưu vào database
        assertEquals(initialCount + 1, foodItemRepository.count());
        
        // Verify food item details trong database
        List<FoodItem> items = foodItemRepository.findAll();
        FoodItem savedItem = items.get((int)initialCount);
        assertNotNull(savedItem.getId());
        assertEquals("Phở Bò", savedItem.getName());
        assertEquals("Phở bò truyền thống", savedItem.getDescription());
        assertEquals("50000", savedItem.getPrice());
        assertEquals("pho-bo.jpg", savedItem.getImage());
        assertEquals(category.getCategoryId(), savedItem.getCategory().getCategoryId());
    }

    /**
     * Test case TC-FOODITEM-SERVICE-002: Kiểm tra saveFoodItem với tên món null.
     * Expected: Lỗi vì tên món là bắt buộc trong hệ thống khách sạn.
     */
    @Test
    public void testSaveFoodItem_NullName_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo category hợp lệ
        Category category = createValidCategory();
        categoryRepository.save(category);
        em.flush();

        // Tạo FoodItem với tên null - vi phạm logic nghiệp vụ
        FoodItem foodItem = new FoodItem(null, "Mô tả món", "30000", "image.jpg", category);

        long initialCount = foodItemRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì tên món là bắt buộc
            foodItemService.saveFoodItem(foodItem);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (foodItemRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu món ăn mà không có tên - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-003: Kiểm tra saveFoodItem với giá âm.
     * Expected: Lỗi vì giá món ăn phải > 0 trong nghiệp vụ khách sạn.
     */
    @Test
    public void testSaveFoodItem_NegativePrice_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo category hợp lệ
        Category category = createValidCategory();
        categoryRepository.save(category);
        em.flush();

        // Tạo FoodItem với giá âm - vi phạm logic nghiệp vụ
        FoodItem foodItem = new FoodItem("Cơm chiên", "Cơm chiên thập cẩm", "-25000", "com-chien.jpg", category);

        long initialCount = foodItemRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì giá âm không hợp lệ
            foodItemService.saveFoodItem(foodItem);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (foodItemRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu món ăn với giá âm - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-004: Kiểm tra saveFoodItem với giá = 0.
     * Expected: Lỗi vì giá món ăn phải > 0 trong nghiệp vụ khách sạn.
     */
    @Test
    public void testSaveFoodItem_ZeroPrice_ShouldFail() {
        // Chuẩn bị dữ liệu test: Tạo category hợp lệ
        Category category = createValidCategory();
        categoryRepository.save(category);
        em.flush();

        // Tạo FoodItem với giá = 0 - vi phạm logic nghiệp vụ
        FoodItem foodItem = new FoodItem("Bánh mì", "Bánh mì thịt", "0", "banh-mi.jpg", category);

        long initialCount = foodItemRepository.count();

        try {
            // Gọi service method - Expected: Lỗi vì giá 0 không hợp lệ
            foodItemService.saveFoodItem(foodItem);
            
            // Nếu không có exception, check xem có được lưu không (để phát hiện lỗi)
            if (foodItemRepository.count() == initialCount + 1) {
                fail("BUG DETECTED: Hệ thống cho phép lưu món ăn với giá 0 - vi phạm logic nghiệp vụ khách sạn");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-005: Kiểm tra saveFoodItem với category null.
     * Expected: Lỗi vì category là bắt buộc trong hệ thống khách sạn.
     */
    @Test
    public void testSaveFoodItem_NullCategory_ShouldFail() {
        // Tạo FoodItem với category null - vi phạm logic nghiệp vụ
        FoodItem foodItem = new FoodItem("Bún bò", "Bún bò Huế", "40000", "bun-bo.jpg", null);

        try {
            // Gọi service method - Expected: Exception vì category bắt buộc
            foodItemService.saveFoodItem(foodItem);
            fail("Expected exception when category is null - món ăn phải có category");
        } catch (Exception e) {
            // Expected - đây là lỗi nghiệp vụ đúng
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-006: Kiểm tra getItem với ID hợp lệ.
     * Expected: Trả về món ăn tương ứng.
     */
    @Test
    public void testGetItem_ValidId_ShouldReturnItem() {
        // Chuẩn bị dữ liệu test: Tạo và lưu món ăn
        Category category = createValidCategory();
        categoryRepository.save(category);
        
        FoodItem foodItem = new FoodItem("Gà nướng", "Gà nướng mật ong", "80000", "ga-nuong.jpg", category);
        foodItemRepository.save(foodItem);
        em.flush();

        // Gọi service method
        FoodItem result = foodItemService.getItem(foodItem.getId());

        // Kiểm tra kết quả: Món ăn được trả về đúng
        assertNotNull(result);
        assertEquals(foodItem.getId(), result.getId());
        assertEquals("Gà nướng", result.getName());
        assertEquals("Gà nướng mật ong", result.getDescription());
        assertEquals("80000", result.getPrice());
    }

    /**
     * Test case TC-FOODITEM-SERVICE-007: Kiểm tra getItem với ID không tồn tại.
     * Expected: Trả về null hoặc throw exception.
     */
    @Test
    public void testGetItem_NonExistentId_ShouldReturnNull() {
        // Gọi service method với ID không tồn tại
        try {
            FoodItem result = foodItemService.getItem(99999L);
            
            // Nếu không throw exception thì phải trả về null
            assertNull("Expected null when item not found", result);
        } catch (Exception e) {
            // Cũng chấp nhận được nếu throw exception khi không tìm thấy
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-008: Kiểm tra deleteFoodItem với ID hợp lệ.
     * Expected: Món ăn được xóa khỏi database.
     */
    @Test
    public void testDeleteFoodItem_ValidId_ShouldDelete() {
        // Chuẩn bị dữ liệu test: Tạo và lưu món ăn
        Category category = createValidCategory();
        categoryRepository.save(category);
        
        FoodItem foodItem = new FoodItem("Tôm nướng", "Tôm nướng tiêu", "120000", "tom-nuong.jpg", category);
        foodItemRepository.save(foodItem);
        em.flush();

        long initialCount = foodItemRepository.count();

        // Gọi service method
        foodItemService.deleteFoodItem(foodItem.getId());

        // Kiểm tra kết quả: Món ăn đã được xóa
        assertEquals(initialCount - 1, foodItemRepository.count());
    }

    /**
     * Test case TC-FOODITEM-SERVICE-009: Kiểm tra deleteFoodItem với ID không tồn tại.
     * Expected: Không gây lỗi hệ thống (handle gracefully).
     */
    @Test
    public void testDeleteFoodItem_NonExistentId_ShouldNotCauseError() {
        long initialCount = foodItemRepository.count();

        // Gọi service method với ID không tồn tại
        try {
            foodItemService.deleteFoodItem(99999L);
            
            // Kiểm tra DB không thay đổi
            assertEquals(initialCount, foodItemRepository.count());
        } catch (Exception e) {
            // Nếu throw exception thì cũng chấp nhận được (tùy implementation)
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-010: Kiểm tra loadToSelectOption.
     * Expected: Trả về list tất cả món ăn.
     */
    @Test
    public void testLoadToSelectOption_ShouldReturnAllItems() {
        // Chuẩn bị dữ liệu test: Tạo và lưu nhiều món ăn
        Category category = createValidCategory();
        categoryRepository.save(category);
        
        FoodItem item1 = new FoodItem("Cà ri gà", "Cà ri gà thơm ngon", "45000", "cari-ga.jpg", category);
        FoodItem item2 = new FoodItem("Bò kho", "Bò kho bánh mì", "55000", "bo-kho.jpg", category);
        
        foodItemRepository.save(item1);
        foodItemRepository.save(item2);
        em.flush();

        // Gọi service method
        List<FoodItem> result = foodItemService.loadToSelectOption();

        // Kiểm tra kết quả: List chứa tất cả món ăn
        assertNotNull(result);
        assertTrue("Should return at least 2 items", result.size() >= 2);
    }

    /**
     * Test case TC-FOODITEM-SERVICE-011: Kiểm tra getListFoodItem với text tìm kiếm.
     * Expected: Trả về page chứa món ăn match với text search.
     */
    @Test
    public void testGetListFoodItem_WithSearchText_ShouldReturnMatchingItems() {
        // Chuẩn bị dữ liệu test: Tạo món ăn có tên chứa "gà"
        Category category = createValidCategory();
        categoryRepository.save(category);
        
        FoodItem item1 = new FoodItem("Cà ri gà", "Cà ri gà thơm ngon", "45000", "cari-ga.jpg", category);
        FoodItem item2 = new FoodItem("Gà nướng", "Gà nướng mật ong", "80000", "ga-nuong.jpg", category);
        FoodItem item3 = new FoodItem("Bò nướng", "Bò nướng lá lốt", "90000", "bo-nuong.jpg", category);
        
        foodItemRepository.save(item1);
        foodItemRepository.save(item2);
        foodItemRepository.save(item3);
        em.flush();

        // Gọi service method với text search "gà"
        Pageable pageable = PageRequest.of(0, 10);
        Page<FoodItemDto> result = foodItemService.getListFoodItem(pageable, "gà");

        // Kiểm tra kết quả: Chỉ trả về món chứa "gà"
        assertNotNull(result);
        assertTrue("Should find items containing 'gà'", result.getContent().size() >= 2);
        
        // Verify các item trả về đều chứa "gà" trong tên
        for (FoodItemDto item : result.getContent()) {
            assertTrue("Item name should contain 'gà'", 
                item.getName().toLowerCase().contains("gà"));
        }
    }

    /**
     * Test case TC-FOODITEM-SERVICE-012: Kiểm tra getListFoodItem với pagination.
     * Expected: Phân trang hoạt động đúng.
     */
    @Test
    public void testGetListFoodItem_Pagination_ShouldWorkCorrectly() {
        // Chuẩn bị dữ liệu test: Tạo nhiều món ăn
        Category category = createValidCategory();
        categoryRepository.save(category);
        
        for (int i = 1; i <= 15; i++) {
            FoodItem item = new FoodItem("Món " + i, "Mô tả món " + i, (20000 + i * 1000) + "", "mon" + i + ".jpg", category);
            foodItemRepository.save(item);
        }
        em.flush();

        // Test pagination: page 0, size 5
        Pageable pageable1 = PageRequest.of(0, 5);
        Page<FoodItemDto> page1 = foodItemService.getListFoodItem(pageable1, "");
        
        // Test pagination: page 1, size 5
        Pageable pageable2 = PageRequest.of(1, 5);
        Page<FoodItemDto> page2 = foodItemService.getListFoodItem(pageable2, "");

        // Kiểm tra pagination hoạt động đúng
        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(5, page1.getSize());
        assertEquals(5, page2.getSize());
        assertTrue("Should have multiple pages", page1.getTotalPages() >= 3);
    }

    /**
     * Helper method tạo Category hợp lệ cho test.
     */
    private Category createValidCategory() {
        Category category = new Category();
        category.setCategoryName("Món chính");
        return category;
    }
}
