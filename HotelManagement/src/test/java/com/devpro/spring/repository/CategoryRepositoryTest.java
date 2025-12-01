package com.devpro.spring.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.Category;

/**
 * Lớp test integration cho CategoryRepository.
 * Test các chức năng quản lý danh mục món ăn trong hệ thống quản lý khách sạn.
 * Bao gồm: tạo, tìm kiếm, cập nhật, xóa category.
 * Sử dụng DB H2 để test thực tế, đảm bảo check DB operations.
 * Mỗi test case rollback transaction để giữ DB sạch.
 * Test theo logic nghiệp vụ: category phải có tên hợp lệ, không trùng lặp, etc.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Test case TC-CATEGORY-REPO-001: Kiểm tra count ban đầu.
     * Expected: Count = 0 vì chưa có category nào trong DB.
     */
    @Test
    public void testCount_Initially_ShouldReturnZero() {
        assertEquals(0, categoryRepository.count());
    }

    /**
     * Test case TC-CATEGORY-REPO-002: Kiểm tra save category hợp lệ.
     * Expected: Category được save thành công, count tăng lên 1.
     */
    @Test
    public void testSave_ValidCategory_ShouldSaveSuccessfully() {
        Category category = new Category();
        category.setCategoryName("Main Course");
        category = categoryRepository.save(category);

        assertNotNull(category.getCategoryId());
        assertEquals(1, categoryRepository.count());
    }

    /**
     * Test case TC-CATEGORY-REPO-003: Kiểm tra findById sau save.
     * Expected: Tìm thấy category với thông tin đúng.
     */
    @Test
    public void testFindById_AfterSave_ShouldReturnCategory() {
        Category category = new Category();
        category.setCategoryName("Beverages");
        category = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(category.getCategoryId());
        assertTrue(found.isPresent());
        assertEquals("Beverages", found.get().getCategoryName());
    }

    /**
     * Test case TC-CATEGORY-REPO-004: Kiểm tra findAll sau save nhiều.
     * Expected: Trả về list với size đúng, chứa các category đã save.
     */
    @Test
    public void testFindAll_AfterSaveMultiple_ShouldReturnList() {
        Category category1 = new Category();
        category1.setCategoryName("Appetizers");
        Category category2 = new Category();
        category2.setCategoryName("Desserts");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();
        assertEquals(2, categories.size());
    }

    /**
     * Test case TC-CATEGORY-REPO-005: Kiểm tra delete category.
     * Expected: Delete thành công, count giảm về 0.
     */
    @Test
    public void testDelete_Category_ShouldDeleteSuccessfully() {
        Category category = new Category();
        category.setCategoryName("Snacks");
        category = categoryRepository.save(category);

        categoryRepository.deleteById(category.getCategoryId());
        assertEquals(0, categoryRepository.count());
    }

    /**
     * Test case TC-CATEGORY-REPO-006: Kiểm tra save category với name rỗng.
     * Expected: Save thành công vì name có thể rỗng theo logic nghiệp vụ.
     */
    @Test
    public void testSave_Category_WithEmptyName_ShouldSaveSuccessfully() {
        Category category = new Category();
        category.setCategoryName("");
        category = categoryRepository.save(category);

        assertNotNull(category.getCategoryId());
        assertEquals("", category.getCategoryName());
    }

    /**
     * Test case TC-CATEGORY-REPO-007: Kiểm tra save category với name dài.
     * Expected: Save thành công vì name có thể dài.
     */
    @Test
    public void testSave_Category_WithLongName_ShouldSaveSuccessfully() {
        Category category = new Category();
        category.setCategoryName("Very Long Category Name For Testing Purposes");
        category = categoryRepository.save(category);

        assertNotNull(category.getCategoryId());
        assertEquals("Very Long Category Name For Testing Purposes", category.getCategoryName());
    }

    /**
     * Test case TC-CATEGORY-REPO-008: Kiểm tra findById với ID không tồn tại.
     * Expected: Trả về empty Optional.
     */
    @Test
    public void testFindById_NonExistentId_ShouldReturnEmpty() {
        Optional<Category> found = categoryRepository.findById(99999L);
        assertTrue(found.isEmpty());
    }

    /**
     * Test case TC-CATEGORY-REPO-009: Kiểm tra findAll khi empty.
     * Expected: Trả về empty list.
     */
    @Test
    public void testFindAll_WhenEmpty_ShouldReturnEmptyList() {
        List<Category> categories = categoryRepository.findAll();
        assertTrue(categories.isEmpty());
    }

    /**
     * Test case TC-CATEGORY-REPO-010: Kiểm tra save category với name null.
     * Expected: Save thành công hoặc handle null.
     */
    @Test
    public void testSave_Category_WithNullName_ShouldHandle() {
        Category category = new Category();
        category.setCategoryName(null);
        category = categoryRepository.save(category);

        assertNotNull(category.getCategoryId());
        assertEquals(null, category.getCategoryName());
    }

    /**
     * Test case TC-CATEGORY-REPO-011: Kiểm tra update category name.
     * Expected: Update thành công.
     */
    @Test
    public void testUpdate_CategoryName_ShouldUpdateSuccessfully() {
        Category category = new Category();
        category.setCategoryName("Old Name");
        category = categoryRepository.save(category);

        category.setCategoryName("New Name");
        category = categoryRepository.save(category);

        Optional<Category> updated = categoryRepository.findById(category.getCategoryId());
        assertTrue(updated.isPresent());
        assertEquals("New Name", updated.get().getCategoryName());
    }
}