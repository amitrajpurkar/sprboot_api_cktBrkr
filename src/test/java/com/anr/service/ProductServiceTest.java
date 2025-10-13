package com.anr.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Objects;

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
}
