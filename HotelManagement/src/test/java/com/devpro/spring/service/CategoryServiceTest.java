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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.Category;
import com.devpro.spring.repository.CategoryRepository;

/**
 * Lớp test integration cho CategoryService.
 * Test các chức năng quản lý danh mục món ăn trong hệ thống quản lý khách sạn.
 * Bao gồm: validation dữ liệu theo logic nghiệp vụ thực tế, CRUD operations, tìm kiếm danh mục.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ thực tế của khách sạn: category phải có tên, ID phải hợp lệ, etc.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    /**
     * Test case TC-CATEGORY-SERVICE-001: Kiểm tra loadListCategories khi có categories trong database.
     * Expected: Trả về list chứa tất cả categories.
     */
    @Test
    public void testLoadListCategories_WithExistingCategories_ShouldReturnAllCategories() {
        // Chuẩn bị dữ liệu test: Tạo và lưu categories
        Category category1 = new Category();
        category1.setCategoryName("Món chính");
        
        Category category2 = new Category();
        category2.setCategoryName("Đồ uống");
        
        Category category3 = new Category();
        category3.setCategoryName("Tráng miệng");
        
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        em.flush();

        // Gọi service method
        List<Category> result = categoryService.loadListCategories();

        // Kiểm tra kết quả: List chứa tất cả categories
        assertNotNull("Result should not be null", result);
        assertTrue("Should return at least 3 categories", result.size() >= 3);
        
        // Verify các category đã được load đúng
        boolean foundMonChinh = false, foundDoUong = false, foundTrangMieng = false;
        for (Category cat : result) {
            if ("Món chính".equals(cat.getCategoryName())) foundMonChinh = true;
            if ("Đồ uống".equals(cat.getCategoryName())) foundDoUong = true;
            if ("Tráng miệng".equals(cat.getCategoryName())) foundTrangMieng = true;
        }
        assertTrue("Should contain 'Món chính' category", foundMonChinh);
        assertTrue("Should contain 'Đồ uống' category", foundDoUong);
        assertTrue("Should contain 'Tráng miệng' category", foundTrangMieng);
    }

    /**
     * Test case TC-CATEGORY-SERVICE-002: Kiểm tra loadListCategories khi không có categories trong database.
     * Expected: Trả về empty list (không null).
     */
    @Test
    public void testLoadListCategories_WithNoCategories_ShouldReturnEmptyList() {
        // Đảm bảo database trống (do @Transactional sẽ rollback)
        
        // Gọi service method
        List<Category> result = categoryService.loadListCategories();

        // Kiểm tra kết quả: Empty list nhưng không null
        assertNotNull("Result should not be null even when no categories exist", result);
        assertEquals("Should return empty list when no categories exist", 0, result.size());
    }

    /**
     * Test case TC-CATEGORY-SERVICE-003: Kiểm tra getOne với ID hợp lệ.
     * Expected: Trả về category tương ứng với ID.
     */
    @Test
    public void testGetOne_ValidId_ShouldReturnCategory() {
        // Chuẩn bị dữ liệu test: Tạo và lưu category
        Category category = new Category();
        category.setCategoryName("Món khai vị");
        
        categoryRepository.save(category);
        em.flush();

        // Gọi service method
        Category result = categoryService.getOne(category.getCategoryId());

        // Kiểm tra kết quả: Category được trả về đúng
        assertNotNull("Result should not be null for valid ID", result);
        assertEquals("Should return correct category ID", category.getCategoryId(), result.getCategoryId());
        assertEquals("Should return correct category name", "Món khai vị", result.getCategoryName());
    }

    /**
     * Test case TC-CATEGORY-SERVICE-004: Kiểm tra getOne với ID không tồn tại.
     * Expected: Trả về null hoặc throw exception theo logic nghiệp vụ.
     */
    @Test
    public void testGetOne_NonExistentId_ShouldHandleGracefully() {
        // Gọi service method với ID không tồn tại
        try {
            Category result = categoryService.getOne(99999L);
            
            // Nếu không throw exception thì phải trả về null
            assertNull("Expected null when category ID not found", result);
        } catch (Exception e) {
            // Cũng chấp nhận được nếu throw exception khi không tìm thấy
            // Đây là behavior hợp lệ theo logic nghiệp vụ
        }
    }

    /**
     * Test case TC-CATEGORY-SERVICE-005: Kiểm tra getOne với ID null.
     * Expected: Lỗi vì ID null không hợp lệ trong hệ thống khách sạn.
     */
    @Test
    public void testGetOne_NullId_ShouldFail() {
        try {
            // Gọi service method với ID null - Expected: Exception
            Category result = categoryService.getOne(null);
            
            // Nếu không có exception thì đây là lỗi nghiệp vụ
            if (result != null) {
                fail("BUG DETECTED: Hệ thống không validate ID null - vi phạm logic nghiệp vụ");
            }
        } catch (Exception e) {
            // Expected - đây là behavior đúng theo logic nghiệp vụ khách sạn
            // ID null không được phép trong hệ thống quản lý category
        }
    }

    /**
     * Test case TC-CATEGORY-SERVICE-006: Kiểm tra getOne với ID âm.
     * Expected: Lỗi hoặc trả về null vì ID âm không hợp lệ.
     */
    @Test
    public void testGetOne_NegativeId_ShouldFail() {
        try {
            // Gọi service method với ID âm - không hợp lệ theo logic nghiệp vụ
            Category result = categoryService.getOne(-1L);
            
            // Nếu không throw exception thì phải trả về null
            assertNull("Expected null for negative ID", result);
        } catch (Exception e) {
            // Cũng chấp nhận được nếu throw exception với ID âm
        }
    }

    /**
     * Test case TC-CATEGORY-SERVICE-007: Kiểm tra loadListCategories performance với nhiều categories.
     * Expected: Load được tất cả categories một cách hiệu quả.
     */
    @Test
    public void testLoadListCategories_WithManyCategories_ShouldReturnAll() {
        // Chuẩn bị dữ liệu test: Tạo nhiều categories
        for (int i = 1; i <= 10; i++) {
            Category category = new Category();
            category.setCategoryName("Danh mục " + i);
            categoryRepository.save(category);
        }
        em.flush();

        // Gọi service method
        List<Category> result = categoryService.loadListCategories();

        // Kiểm tra kết quả: Load được tất cả categories
        assertNotNull("Result should not be null", result);
        assertTrue("Should load at least 10 categories", result.size() >= 10);
        
        // Verify một số categories cụ thể
        boolean foundCategory1 = false, foundCategory5 = false, foundCategory10 = false;
        for (Category cat : result) {
            if ("Danh mục 1".equals(cat.getCategoryName())) foundCategory1 = true;
            if ("Danh mục 5".equals(cat.getCategoryName())) foundCategory5 = true;
            if ("Danh mục 10".equals(cat.getCategoryName())) foundCategory10 = true;
        }
        assertTrue("Should contain 'Danh mục 1'", foundCategory1);
        assertTrue("Should contain 'Danh mục 5'", foundCategory5);
        assertTrue("Should contain 'Danh mục 10'", foundCategory10);
    }

    /**
     * Test case TC-CATEGORY-SERVICE-008: Kiểm tra tính toàn vẹn của categories trong hotel business.
     * Expected: Tất cả categories có tên hợp lệ và ID được generate.
     */
    @Test
    public void testLoadListCategories_CategoryIntegrity_ShouldHaveValidData() {
        // Chuẩn bị dữ liệu test: Tạo categories với các tên khách sạn thực tế
        Category appetizer = new Category();
        appetizer.setCategoryName("Món khai vị");
        
        Category mainCourse = new Category();
        mainCourse.setCategoryName("Món chính");
        
        Category beverage = new Category();
        beverage.setCategoryName("Đồ uống");
        
        categoryRepository.save(appetizer);
        categoryRepository.save(mainCourse);
        categoryRepository.save(beverage);
        em.flush();

        // Gọi service method
        List<Category> result = categoryService.loadListCategories();

        // Kiểm tra tính toàn vẹn dữ liệu
        assertNotNull("Categories list should not be null", result);
        assertTrue("Should have at least 3 categories", result.size() >= 3);
        
        // Verify mỗi category có dữ liệu hợp lệ
        for (Category category : result) {
            assertNotNull("Category ID should not be null", category.getCategoryId());
            assertNotNull("Category name should not be null", category.getCategoryName());
            assertTrue("Category ID should be positive", category.getCategoryId() > 0);
            assertTrue("Category name should not be empty", 
                category.getCategoryName() != null && !category.getCategoryName().trim().isEmpty());
        }
    }
}