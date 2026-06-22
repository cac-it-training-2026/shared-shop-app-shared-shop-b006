const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();

  try {
    console.log('Navigating to login page...');
    await page.goto('http://localhost:55000/shared_shop/login');
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'test');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/client/item/list**');
    console.log('Logged in successfully.');

    // 1. Go to item detail page
    console.log('Navigating to item detail page...');
    await page.goto('http://localhost:55000/shared_shop/client/item/detail/1');
    await page.screenshot({ path: '/home/jules/verification/screenshots/item_detail_logged_in.png' });

    // 2. Add to basket with quantity 2
    console.log('Adding item to basket...');
    await page.click('.plus-btn'); // 1 -> 2
    await page.screenshot({ path: '/home/jules/verification/screenshots/item_detail_qty2.png' });
    await page.click('button[type="submit"]');

    await page.waitForURL('**/client/basket/list');
    console.log('Navigated to basket list.');
    await page.screenshot({ path: '/home/jules/verification/screenshots/basket_list_after_add.png' });

    // 3. Update quantity in basket (2 -> 3)
    console.log('Updating quantity in basket...');
    await page.click('.plus-btn'); // 2 -> 3
    await page.click('button:has-text("更新")');
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: '/home/jules/verification/screenshots/basket_list_updated.png' });

    // 4. Delete item by setting quantity to 0
    console.log('Deleting item from basket (setting qty to 0)...');
    await page.click('.minus-btn'); // 3 -> 2
    await page.click('.minus-btn'); // 2 -> 1
    await page.click('.minus-btn'); // 1 -> 0
    await page.click('button:has-text("更新")');
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: '/home/jules/verification/screenshots/basket_list_deleted.png' });

  } catch (error) {
    console.error('Error during verification:', error);
    await page.screenshot({ path: '/home/jules/verification/screenshots/error.png' });
  } finally {
    await browser.close();
  }
})();
