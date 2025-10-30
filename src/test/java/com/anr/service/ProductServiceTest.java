package com.anr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.anr.localmdb.model.Product;
import com.anr.localmdb.model.Product.ProductBuilder;
import com.anr.localmdb.repository.ProductRepository;

/**
 * https://gitmoji.carloscuesta.me/
 *
 * https://github.com/slashsbin/styleguide-git-commit-message
 *
 * For service layer it is a good technique to unit test each method; we can make use of mockito to
 * mock repository and backend classes; we need not make end to end connection with backend
 * components; rather just validate if the service methods themselves are executing the code as
 * anticipated
 *
 * @author amitr
 *
 */
// @SpringBootTest - Not needed for pure unit tests with mocks
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService sut;
    // @Autowired
    // private Gson gson;
    // @MockBean
    @Mock
    private ProductRepository mockRepo;

    @Test
    void insert_one_product() {
        Product mockProduct = getMock("ironman", "plastic figuring toy of an avenger superhero", "$5.49");
        Product expectedProduct = getMock("ironman", "plastic figuring toy of an avenger superhero", "$5.49");

        when(mockRepo.save(mockProduct)).thenReturn(expectedProduct);
        Product savedProduct = sut.saveOne(mockProduct);

        assertNotNull(savedProduct, "the saved product was null");
        assertTrue(Objects.equals(savedProduct, expectedProduct));
    }

    private Product getMock(String name, String desc, String price) {
        String id = RandomStringUtils.randomAlphabetic(10);
        return new ProductBuilder(id, name).desc(desc).price(price).build();
    }

    // ========================================================================
    // TRANSACTION MANAGEMENT TESTS - Added for CRITICAL-003 fix
    // ========================================================================

    @Test
    void test_saveBatch_success() {
        // Arrange
        Product product1 = getMock("Product1", "Description 1", "$10.00");
        Product product2 = getMock("Product2", "Description 2", "$20.00");
        List<Product> productsToSave = Arrays.asList(product1, product2);
        
        when(mockRepo.saveAll(productsToSave)).thenReturn(productsToSave);
        
        // Act
        List<Product> savedProducts = sut.saveBatch(productsToSave);
        
        // Assert
        assertNotNull(savedProducts);
        assertEquals(2, savedProducts.size());
        verify(mockRepo, times(1)).saveAll(productsToSave);
    }

    @Test
    void test_saveBatch_emptyList() {
        // Arrange
        List<Product> emptyList = Collections.emptyList();
        when(mockRepo.saveAll(emptyList)).thenReturn(emptyList);
        
        // Act
        List<Product> savedProducts = sut.saveBatch(emptyList);
        
        // Assert
        assertNotNull(savedProducts);
        assertTrue(savedProducts.isEmpty());
        verify(mockRepo, times(1)).saveAll(emptyList);
    }

    @Test
    void test_findById_success() {
        // Arrange
        String productId = "P001";
        Product expectedProduct = getMock("Test Product", "Test Description", "$15.00");
        expectedProduct.setId(productId);
        
        when(mockRepo.findById(productId)).thenReturn(Optional.of(expectedProduct));
        
        // Act
        Product foundProduct = sut.findById(productId);
        
        // Assert
        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());
        assertEquals("Test Product", foundProduct.getName());
        verify(mockRepo, times(1)).findById(productId);
    }

    @Test
    void test_findById_notFound() {
        // Arrange
        String productId = "P999";
        when(mockRepo.findById(productId)).thenReturn(Optional.empty());
        
        // Act
        Product foundProduct = sut.findById(productId);
        
        // Assert
        assertEquals(Product.EMPTY, foundProduct);
        verify(mockRepo, times(1)).findById(productId);
    }

    @Test
    void test_findByExactName_success() {
        // Arrange
        String productName = "Test Product";
        Product product1 = getMock(productName, "Description 1", "$10.00");
        Product product2 = getMock(productName, "Description 2", "$20.00");
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        
        when(mockRepo.findProductsByName(productName)).thenReturn(expectedProducts);
        
        // Act
        List<Product> foundProducts = sut.findByExactName(productName);
        
        // Assert
        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        verify(mockRepo, times(1)).findProductsByName(productName);
    }

    @Test
    void test_findByDescContaining_success() {
        // Arrange
        String searchText = "toy";
        Product product1 = getMock("Product1", "plastic toy", "$10.00");
        Product product2 = getMock("Product2", "wooden toy", "$15.00");
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        
        when(mockRepo.findProductsWithDescriptionContaining(searchText)).thenReturn(expectedProducts);
        
        // Act
        List<Product> foundProducts = sut.findByDescContaining(searchText);
        
        // Assert
        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        verify(mockRepo, times(1)).findProductsWithDescriptionContaining(searchText);
    }

    @Test
    void test_findAll_success() {
        // Arrange
        Product product1 = getMock("Product1", "Description 1", "$10.00");
        Product product2 = getMock("Product2", "Description 2", "$20.00");
        Product product3 = getMock("Product3", "Description 3", "$30.00");
        List<Product> allProducts = Arrays.asList(product1, product2, product3);
        
        when(mockRepo.findAll()).thenReturn(allProducts);
        
        // Act
        List<Product> foundProducts = sut.findAll();
        
        // Assert
        assertNotNull(foundProducts);
        assertEquals(3, foundProducts.size());
        verify(mockRepo, times(1)).findAll();
    }

    @Test
    void test_findAll_emptyList() {
        // Arrange
        when(mockRepo.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Product> foundProducts = sut.findAll();
        
        // Assert
        assertNotNull(foundProducts);
        assertTrue(foundProducts.isEmpty());
        verify(mockRepo, times(1)).findAll();
    }

    @Test
    void test_updateProduct_success() {
        // Arrange
        String productId = "P001";
        Product existingProduct = getMock("Old Name", "Old Description", "$10.00");
        existingProduct.setId(productId);
        existingProduct.setVersion(1L);
        
        Product updatedProduct = getMock("New Name", "New Description", "$20.00");
        updatedProduct.setId(productId);
        updatedProduct.setVersion(1L);
        
        when(mockRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(mockRepo.save(any(Product.class))).thenReturn(updatedProduct);
        
        // Act
        Product result = sut.updateProduct(productId, updatedProduct);
        
        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("New Name", result.getName());
        verify(mockRepo, times(1)).findById(productId);
        verify(mockRepo, times(1)).save(any(Product.class));
    }

    @Test
    void test_updateProduct_notFound() {
        // Arrange
        String productId = "P999";
        Product updateData = getMock("New Name", "New Description", "$20.00");
        
        when(mockRepo.findById(productId)).thenReturn(Optional.empty());
        
        // Act
        Product result = sut.updateProduct(productId, updateData);
        
        // Assert
        assertEquals(Product.EMPTY, result);
        verify(mockRepo, times(1)).findById(productId);
        verify(mockRepo, never()).save(any(Product.class));
    }

    @Test
    void test_updateProduct_preservesVersion() {
        // Arrange
        String productId = "P001";
        Product existingProduct = getMock("Old Name", "Old Description", "$10.00");
        existingProduct.setId(productId);
        existingProduct.setVersion(5L); // Existing version
        
        Product updatedProduct = getMock("New Name", "New Description", "$20.00");
        updatedProduct.setVersion(null); // No version in update data
        
        when(mockRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(mockRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            assertEquals(5L, saved.getVersion(), "Version should be preserved from existing product");
            return saved;
        });
        
        // Act
        Product result = sut.updateProduct(productId, updatedProduct);
        
        // Assert
        assertNotNull(result);
        verify(mockRepo, times(1)).findById(productId);
        verify(mockRepo, times(1)).save(any(Product.class));
    }

    @Test
    void test_deleteProduct_success() {
        // Arrange
        String productId = "P001";
        when(mockRepo.existsById(productId)).thenReturn(true);
        
        // Act
        boolean result = sut.deleteProduct(productId);
        
        // Assert
        assertTrue(result);
        verify(mockRepo, times(1)).existsById(productId);
        verify(mockRepo, times(1)).deleteById(productId);
    }

    @Test
    void test_deleteProduct_notFound() {
        // Arrange
        String productId = "P999";
        when(mockRepo.existsById(productId)).thenReturn(false);
        
        // Act
        boolean result = sut.deleteProduct(productId);
        
        // Assert
        assertFalse(result);
        verify(mockRepo, times(1)).existsById(productId);
        verify(mockRepo, never()).deleteById(anyString());
    }

    @Test
    void test_saveOne_withVersion() {
        // Arrange
        Product productWithVersion = getMock("Versioned Product", "Has version", "$25.00");
        productWithVersion.setVersion(1L);
        
        when(mockRepo.save(productWithVersion)).thenReturn(productWithVersion);
        
        // Act
        Product savedProduct = sut.saveOne(productWithVersion);
        
        // Assert
        assertNotNull(savedProduct);
        assertEquals(1L, savedProduct.getVersion());
        verify(mockRepo, times(1)).save(productWithVersion);
    }

    @Test
    void test_updateProduct_ensuresIdMatches() {
        // Arrange
        String pathId = "P001";
        String bodyId = "P002"; // Different ID in body
        
        Product existingProduct = getMock("Old Name", "Old Description", "$10.00");
        existingProduct.setId(pathId);
        
        Product updateData = getMock("New Name", "New Description", "$20.00");
        updateData.setId(bodyId); // Wrong ID
        
        when(mockRepo.findById(pathId)).thenReturn(Optional.of(existingProduct));
        when(mockRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            assertEquals(pathId, saved.getId(), "ID should be set to path parameter ID");
            return saved;
        });
        
        // Act
        Product result = sut.updateProduct(pathId, updateData);
        
        // Assert
        assertNotNull(result);
        verify(mockRepo, times(1)).findById(pathId);
        verify(mockRepo, times(1)).save(any(Product.class));
    }
}
