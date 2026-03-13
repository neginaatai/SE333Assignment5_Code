package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        mockCart = mock(ShoppingCart.class);
    }

    // ─── RegularCost ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("specification-based: RegularCost returns 0 for empty cart")
    void regularCost_emptyCart_returnsZero() {
        RegularCost rule = new RegularCost();
        double result = rule.priceToAggregate(List.of());
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: RegularCost sums price * quantity for all items")
    void regularCost_multipleItems_returnsSumOfPriceTimesQuantity() {
        RegularCost rule = new RegularCost();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Book", 2, 10.0),   // 20.0
                new Item(ItemType.OTHER, "Pen", 3, 2.0)      //  6.0
        );
        assertThat(rule.priceToAggregate(items)).isEqualTo(26.0);
    }

    @Test
    @DisplayName("structural-based: RegularCost handles single item")
    void regularCost_singleItem_returnsCorrectPrice() {
        RegularCost rule = new RegularCost();
        List<Item> items = List.of(new Item(ItemType.ELECTRONIC, "Phone", 1, 299.99));
        assertThat(rule.priceToAggregate(items)).isEqualTo(299.99);
    }

    // ─── DeliveryPrice ────────────────────────────────────────────────────────

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 0 for empty cart")
    void deliveryPrice_emptyCart_returnsZero() {
        DeliveryPrice rule = new DeliveryPrice();
        assertThat(rule.priceToAggregate(List.of())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 5 for 1-3 items")
    void deliveryPrice_oneToThreeItems_returnsFive() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Book", 1, 5.0),
                new Item(ItemType.OTHER, "Pen", 1, 1.0)
        );
        assertThat(rule.priceToAggregate(items)).isEqualTo(5.0);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 12.5 for 4-10 items")
    void deliveryPrice_fourToTenItems_returnsTwelvePointFive() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0),
                new Item(ItemType.OTHER, "D", 1, 1.0)
        );
        assertThat(rule.priceToAggregate(items)).isEqualTo(12.5);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 20 for more than 10 items")
    void deliveryPrice_moreThanTenItems_returnsTwenty() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = java.util.Collections.nCopies(11,
                new Item(ItemType.OTHER, "X", 1, 1.0));
        assertThat(rule.priceToAggregate(items)).isEqualTo(20.0);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary — exactly 3 items returns 5")
    void deliveryPrice_exactlyThreeItems_returnsFive() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0)
        );
        assertThat(rule.priceToAggregate(items)).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary — exactly 10 items returns 12.5")
    void deliveryPrice_exactlyTenItems_returnsTwelvePointFive() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = java.util.Collections.nCopies(10,
                new Item(ItemType.OTHER, "X", 1, 1.0));
        assertThat(rule.priceToAggregate(items)).isEqualTo(12.5);
    }

    // ─── ExtraCostForElectronics ──────────────────────────────────────────────

    @Test
    @DisplayName("specification-based: ExtraCostForElectronics returns 7.5 when cart has electronic")
    void extraCost_hasElectronic_returnsSevenPointFive() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = List.of(new Item(ItemType.ELECTRONIC, "Laptop", 1, 999.0));
        assertThat(rule.priceToAggregate(items)).isEqualTo(7.5);
    }

    @Test
    @DisplayName("specification-based: ExtraCostForElectronics returns 0 when no electronics")
    void extraCost_noElectronics_returnsZero() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = List.of(new Item(ItemType.OTHER, "Book", 1, 10.0));
        assertThat(rule.priceToAggregate(items)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("structural-based: ExtraCostForElectronics returns 7.5 even with mixed items")
    void extraCost_mixedItems_returnsSevenPointFive() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Book", 1, 10.0),
                new Item(ItemType.ELECTRONIC, "Phone", 1, 500.0)
        );
        assertThat(rule.priceToAggregate(items)).isEqualTo(7.5);
    }

    // ─── Amazon (with mock cart) ──────────────────────────────────────────────

    @Test
    @DisplayName("specification-based: Amazon.calculate aggregates all price rules")
    void amazon_calculate_aggregatesAllRules() {
        List<Item> items = List.of(
                new Item(ItemType.ELECTRONIC, "Phone", 1, 100.0)
        );
        when(mockCart.getItems()).thenReturn(items);

        amazon = new Amazon(mockCart, List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        ));

        // RegularCost: 100, DeliveryPrice: 5 (1 item), ExtraCost: 7.5
        assertThat(amazon.calculate()).isEqualTo(112.5);
    }

    @Test
    @DisplayName("specification-based: Amazon.addToCart delegates to cart")
    void amazon_addToCart_delegatesToCart() {
        amazon = new Amazon(mockCart, List.of());
        Item item = new Item(ItemType.OTHER, "Book", 1, 10.0);
        amazon.addToCart(item);
        verify(mockCart, times(1)).add(item);
    }

    @Test
    @DisplayName("structural-based: Amazon.calculate returns 0 with no rules")
    void amazon_calculate_noRules_returnsZero() {
        when(mockCart.getItems()).thenReturn(List.of());
        amazon = new Amazon(mockCart, List.of());
        assertThat(amazon.calculate()).isEqualTo(0.0);
    }
}
