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
import com.devpro.spring.model.FoodItem;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FoodItemRepositoryTest {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Test case TC-FOOD-ITEM-REPO-001: Kiểm tra count ban đầu.
     * Expected: Count = 0 vì chưa có food item nào trong DB.
     */
    @Test
    public void testCount_Initially_ShouldReturnZero() {
        assertEquals(0, foodItemRepository.count());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-002: Kiểm tra save food item hợp lệ.
     * Expected: Food item được save thành công, count tăng lên 1.
     */
    @Test
    public void testSave_ValidFoodItem_ShouldSaveSuccessfully() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Pizza", "Delicious pizza", "15.99", "pizza.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        assertNotNull(foodItem.getId());
        assertEquals(1, foodItemRepository.count());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-003: Kiểm tra findById sau save.
     * Expected: Tìm thấy food item với thông tin đúng.
     */
    @Test
    public void testFindById_AfterSave_ShouldReturnFoodItem() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Burger", "Tasty burger", "10.50", "burger.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        Optional<FoodItem> found = foodItemRepository.findById(foodItem.getId());
        assertTrue(found.isPresent());
        assertEquals("Burger", found.get().getName());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-004: Kiểm tra findAll sau save nhiều.
     * Expected: Trả về list với size đúng, chứa các food item đã save.
     */
    @Test
    public void testFindAll_AfterSaveMultiple_ShouldReturnList() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem1 = new FoodItem("Pizza", "Delicious pizza", "15.99", "pizza.jpg", category);
        FoodItem foodItem2 = new FoodItem("Burger", "Tasty burger", "10.50", "burger.jpg", category);
        foodItemRepository.save(foodItem1);
        foodItemRepository.save(foodItem2);

        List<FoodItem> foodItems = foodItemRepository.findAll();
        assertEquals(2, foodItems.size());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-005: Kiểm tra delete food item.
     * Expected: Delete thành công, count giảm về 0.
     */
    @Test
    public void testDelete_FoodItem_ShouldDeleteSuccessfully() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Salad", "Fresh salad", "8.00", "salad.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        foodItemRepository.deleteById(foodItem.getId());
        assertEquals(0, foodItemRepository.count());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-006: Kiểm tra save food item với description rỗng.
     * Expected: Save thành công vì description có thể rỗng.
     */
    @Test
    public void testSave_FoodItem_WithEmptyDescription_ShouldSaveSuccessfully() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Drink", "", "5.00", "drink.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        assertNotNull(foodItem.getId());
        assertEquals("", foodItem.getDescription());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-007: Kiểm tra save food item với price lớn.
     * Expected: Save thành công vì price có thể lớn.
     */
    @Test
    public void testSave_FoodItem_WithLargePrice_ShouldSaveSuccessfully() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Expensive Dish", "Very expensive", "99999.99", "expensive.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        assertNotNull(foodItem.getId());
        assertEquals("99999.99", foodItem.getPrice());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-008: Kiểm tra findById với ID không tồn tại.
     * Expected: Trả về empty Optional.
     */
    @Test
    public void testFindById_NonExistentId_ShouldReturnEmpty() {
        Optional<FoodItem> found = foodItemRepository.findById(99999L);
        assertTrue(found.isEmpty());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-009: Kiểm tra findAll khi empty.
     * Expected: Trả về empty list.
     */
    @Test
    public void testFindAll_WhenEmpty_ShouldReturnEmptyList() {
        List<FoodItem> foodItems = foodItemRepository.findAll();
        assertTrue(foodItems.isEmpty());
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-010: Kiểm tra getListFoodItem với text.
     * Expected: Trả về list food item match text trong name hoặc category.
     */
    @Test
    public void testGetListFoodItem_WithText_ShouldReturnMatchingItems() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem1 = new FoodItem("Pizza Margherita", "Cheese pizza", "15.99", "pizza.jpg", category);
        FoodItem foodItem2 = new FoodItem("Burger Deluxe", "Big burger", "12.50", "burger.jpg", category);
        foodItemRepository.save(foodItem1);
        foodItemRepository.save(foodItem2);

        List<Object[]> results = foodItemRepository.getListFoodItem("%Pizza%");
        assertEquals(1, results.size());
        Object[] result = results.get(0);
        assertEquals("Pizza Margherita", result[1]); // Assuming second element is name
    }

    /**
     * Test case TC-FOOD-ITEM-REPO-011: Kiểm tra getOneFoodItem với ID.
     * Expected: Trả về chi tiết food item dưới dạng Object[].
     */
    @Test
    public void testGetOneFoodItem_WithId_ShouldReturnItemDetails() {
        Category category = createValidCategory();
        category = categoryRepository.save(category);

        FoodItem foodItem = new FoodItem("Pasta", "Italian pasta", "13.00", "pasta.jpg", category);
        foodItem = foodItemRepository.save(foodItem);

        Object[] result = foodItemRepository.getOneFoodItem(foodItem.getId());
        assertNotNull(result);
        assertEquals("Pasta", result[1]); // Assuming second element is name
    }

    private Category createValidCategory() {
        Category category = new Category();
        category.setCategoryName("Main Course");
        return category;
    }
}
