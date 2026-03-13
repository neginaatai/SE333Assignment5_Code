package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.resetDatabase();
        cart = new ShoppingCartAdaptor(database);
    }

    @AfterEach
    void tearDown() {
        database.resetDatabase();
    }

    // ─── ShoppingCartAdaptor + Database ──────────────────────────────────────

    @Test
    @DisplayName("specification-based: adding an item persists it to the database")
    void addItem_persistsToDatabase() {
        Item book = new Item(ItemType.OTHER, "Clean Code", 1, 35.0);
        cart.add(book);

        List<Item> items = cart.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Clean Code");
        assertThat(items.get(0).getPricePerUnit()).isEqualTo(35.0);
    }

    @Test
    @DisplayName("specification-based: adding multiple items all appear in getItems()")
    void addMultipleItems_allPersistedToDatabase() {
        cart.add(new Item(ItemType.OTHER, "Book", 1, 10.0));
        cart.add(new Item(ItemType.ELECTRONIC, "Headphones", 2, 50.0));
        cart.add(new Item(ItemType.OTHER, "Pen", 3, 1.5));

        assertThat(cart.getItems()).hasSize(3);
    }

    @Test
    @DisplayName("specification-based: resetDatabase clears all items")
    void resetDatabase_clearsAllItems() {
        cart.add(new Item(ItemType.OTHER, "Book", 1, 10.0));
        database.resetDatabase();

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("structural-based: item type is preserved correctly in database")
    void addItem_electronicType_preservedInDatabase() {
        cart.add(new Item(ItemType.ELECTRONIC, "Laptop", 1, 999.0));

        List<Item> items = cart.getItems();
        assertThat(items.get(0).getType()).isEqualTo(ItemType.ELECTRONIC);
    }

    @Test
    @DisplayName("structural-based: item quantity is preserved correctly in database")
    void addItem_quantityPreservedInDatabase() {
        cart.add(new Item(ItemType.OTHER, "Notebook", 5, 3.0));

        List<Item> items = cart.getItems();
        assertThat(items.get(0).getQuantity()).isEqualTo(5);
    }

    // ─── Amazon + ShoppingCartAdaptor + Database (full stack) ─────────────────

    @Test
    @DisplayName("specification-based: full pipeline — add items, calculate total price")
    void fullPipeline_calculateTotalPrice() {
        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 200.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Case", 1, 15.0));

        // RegularCost: 200 + 15 = 215
        // DeliveryPrice: 5 (2 items → 1-3 bracket)
        // ExtraCostForElectronics: 7.5
        double total = amazon.calculate();
        assertThat(total).isEqualTo(227.5);
    }

    @Test
    @DisplayName("specification-based: full pipeline — no electronics, no extra cost")
    void fullPipeline_noElectronics_noExtraCost() {
        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 12.0));

        // RegularCost: 24, DeliveryPrice: 5, ExtraCost: 0
        assertThat(amazon.calculate()).isEqualTo(29.0);
    }

    @Test
    @DisplayName("specification-based: full pipeline — empty cart returns only 0 total")
    void fullPipeline_emptyCart_returnsZero() {
        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("structural-based: full pipeline — 11 items triggers max delivery price")
    void fullPipeline_elevenItems_maxDeliveryPrice() {
        amazon = new Amazon(cart, List.of(
                new RegularCost(),
                new DeliveryPrice()
        ));

        for (int i = 0; i < 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }

        // RegularCost: 11 * 1.0 = 11, DeliveryPrice: 20
        assertThat(amazon.calculate()).isEqualTo(31.0);
    }

    @Test
    @DisplayName("structural-based: database isolation — each test starts with clean state")
    void databaseIsolation_freshCartEachTest() {
        cart.add(new Item(ItemType.OTHER, "ShouldNotCarryOver", 1, 5.0));
        database.resetDatabase();

        // After reset, cart should be empty
        assertThat(cart.getItems()).isEmpty();
    }
}