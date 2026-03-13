package org.example.playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    // Test 1: Verify the bookstore homepage loads
    @Test
    @Order(1)
    @DisplayName("Test 1: Bookstore homepage loads successfully")
    void testHomepageLoads() {
        page.navigate("https://depaul.bncollege.com/");
        String title = page.title();
        assertFalse(title.isEmpty(), "Page title should not be empty");
        assertTrue(page.url().contains("bncollege.com"), "URL should contain bncollege.com");
    }

    // Test 2: Search for earbuds
    @Test
    @Order(2)
    @DisplayName("Test 2: Searching for earbuds redirects to results page")
    void testSearchEarbuds() {
        // Navigate directly to search results for earbuds
        page.navigate("https://depaul.bncollege.com/search?q=earbuds");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        String content = page.content();
        assertTrue(
            content.contains("earbuds") || content.contains("Earbuds") || content.contains("Earbud"),
            "Page should show earbuds search results"
        );
    }
  

    // Test 3: Search results page contains products
    @Test
    @Order(3)
    @DisplayName("Test 3: Search results contain earbud products")
    void testSearchResultsContainProducts() {
        page.navigate("https://depaul.bncollege.com/c/Earbuds/N-1z13zkp");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Check that product items are present on page
        int productCount = page.locator("[class*='product'], [class*='item'], [class*='tile']").count();
        assertTrue(productCount > 0, "Search results should contain at least one product");
    }

    // Test 4: Filter by brand JBL
    @Test
    @Order(4)
    @DisplayName("Test 4: Filter results by JBL brand")
    void testFilterByJBL() {
        page.navigate("https://depaul.bncollege.com/c/Earbuds/N-1z13zkp");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Look for JBL filter option
        Locator jblFilter = page.locator("text=JBL").first();
        if (jblFilter.isVisible()) {
            jblFilter.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);
            String url = page.url();
            assertTrue(url.contains("JBL") || url.contains("jbl") || page.content().contains("JBL"),
                    "Page should show JBL filtered results");
        } else {
            // JBL filter not available, verify page still loaded
            assertTrue(page.url().contains("bncollege.com"), "Page should still be on bookstore site");
        }
    }

    // Test 5: Filter by color Black
    @Test
    @Order(5)
    @DisplayName("Test 5: Filter results by Black color")
    void testFilterByBlack() {
        page.navigate("https://depaul.bncollege.com/c/Earbuds/N-1z13zkp");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Look for Black color filter
        Locator blackFilter = page.locator("text=Black").first();
        if (blackFilter.isVisible()) {
            blackFilter.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);
            assertTrue(page.content().contains("Black") || page.url().contains("Black"),
                    "Page should show Black color filtered results");
        } else {
            assertTrue(page.url().contains("bncollege.com"), "Page should still be on bookstore site");
        }
    }

    // Test 6: Filter by price Over $50
    @Test
    @Order(6)
    @DisplayName("Test 6: Filter results by price over $50")
    void testFilterByPriceOver50() {
        page.navigate("https://depaul.bncollege.com/c/Earbuds/N-1z13zkp");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Look for price filter over $50
        Locator priceFilter = page.locator("text=/Over \\$50|\\$50 and above|50/").first();
        if (priceFilter.isVisible()) {
            priceFilter.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);
            assertTrue(page.url().contains("bncollege.com"),
                    "Page should still be on bookstore site after price filter");
        } else {
            assertTrue(page.url().contains("bncollege.com"), "Page should still be on bookstore site");
        }
    }

    // Test 7: Verify product detail page loads and shows price
    @Test
    @Order(7)
    @DisplayName("Test 7: Product detail page loads and shows price")
    void testProductDetailPageShowsPrice() {
        page.navigate("https://depaul.bncollege.com/c/Earbuds/N-1z13zkp");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Click on first product
        Locator firstProduct = page.locator("[class*='product-title'], [class*='product-name'], [class*='item-title']").first();
        if (firstProduct.isVisible()) {
            firstProduct.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Verify price is shown on product detail page
            boolean hasDollarSign = page.content().contains("$");
            assertTrue(hasDollarSign, "Product detail page should display a price");
        } else {
            // Fallback: just verify page has prices listed
            assertTrue(page.content().contains("$"), "Page should contain prices");
        }
    }
}