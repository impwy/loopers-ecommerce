package com.loopers.application.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.InventoryRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.InventoryStatus;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.interfaces.api.inventory.dto.InventoryV1Dto.Request.CreateInventoryRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class InventoryFacadeIntegrationTest {

    @Autowired
    private InventoryFacade inventoryFacade;

    @Autowired
    private InventoryRegister inventoryRegister;

    @MockitoSpyBean
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    Brand brand;
    Product product;

    @BeforeEach
    void setUp() {
        brand = brandRepository.create(BrandFixture.createBrand());
        product = ProductFixture.createProduct(brand);
    }

    @DisplayName("존재하지 않는 상품으로 재고 생성 요청 시 실패")
    @Test
    void create_inventory_fail_when_product_is_not_exist() {
        CreateInventoryRequest createInventoryRequest = CreateInventoryRequest.of(-1L, 1000L);
        CoreException coreException = assertThrows(CoreException.class, () -> inventoryFacade.createInventory(createInventoryRequest));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }

    @DisplayName("음수로 재고 생성 요청 시 실패")
    @Test
    void create_inventory_fail_when_quantity_is_negative() {
        Product savedProduct = productRepository.save(product);
        CreateInventoryRequest createInventoryRequest = CreateInventoryRequest.of(savedProduct.getId(), -1L);

        CoreException coreException = assertThrows(CoreException.class, () -> inventoryFacade.createInventory(createInventoryRequest));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("재고 생성 성공 테스트")
    @Test
    void create_inventory_test() {
        Product savedProduct = productRepository.save(product);
        CreateInventoryRequest createInventoryRequest = CreateInventoryRequest.of(savedProduct.getId(), 1000L);

        Inventory expected = inventoryFacade.createInventory(createInventoryRequest);

        assertThat(expected.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(expected.getQuantity()).isEqualTo(1000L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.IN_SALE);
    }

    @DisplayName("재고가 0일 때 재고 감소 실패 테스트")
    @Test
    void create_inventory_fail_when_inventory_zero_test() {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        Inventory inventory = Inventory.create(productId, 0L);
        inventoryRepository.save(inventory);

        CoreException coreException = assertThrows(CoreException.class, () -> inventoryFacade.decrease(productId, 1L));
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("잘못 된 재고 감소 요청 값 실패 테스트")
    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -2})
    void decrease_inventory_fail_when_quantity_is_wrong_test(long quantity) {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        Inventory inventory = Inventory.create(productId, 100L);

        inventoryRepository.save(inventory);

        CoreException coreException = assertThrows(CoreException.class, () -> inventoryFacade.decrease(productId, quantity));
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("재고 감소 요청 성공")
    @Test
    void decrease_inventory_test() {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        Inventory inventory = Inventory.create(productId, 100L);

        inventoryRepository.save(inventory);

        Inventory expected = inventoryFacade.decrease(productId, 1L);

        assertThat(expected.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(expected.getQuantity()).isEqualTo(99L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.IN_SALE);
    }

    @DisplayName("재고 감소 요청 성공 후 재고 0일 시 품절 상태 변경")
    @Test
    void decrease_inventory_status_soldout_test() {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        Inventory inventory = Inventory.create(productId, 10L);

        inventoryRepository.save(inventory);

        Inventory expected = inventoryFacade.decrease(productId, 10L);

        assertThat(expected.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(expected.getQuantity()).isEqualTo(0L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.SOLD_OUT);
    }

    @DisplayName("상품 ID와 재고 ID가 달라도 일괄 재고 차감에 성공한다")
    @Test
    void decrease_products_when_product_id_differs_from_inventory_id() {
        productRepository.save(product);
        Product savedProduct = productRepository.save(ProductFixture.createProduct(brand));
        Inventory inventory = inventoryRepository.save(Inventory.create(savedProduct.getId(), 100L));

        assertThat(inventory.getId()).isNotEqualTo(savedProduct.getId());

        inventoryRegister.decreaseProducts(List.of(new DecreaseInventoryRequest(savedProduct.getId(), 10L)));

        Inventory expected = inventoryRepository.findByProductId(savedProduct.getId()).orElseThrow();
        assertThat(expected.getQuantity()).isEqualTo(90L);
    }
}
