import { test, expect } from '@playwright/test';

// Helper to create a logged-in user
async function loginAsNewUser(page: import('@playwright/test').Page) {
  const email = `test-${Date.now()}@example.com`;
  const password = 'SecurePass123!';
  const displayName = 'Test User';

  // Register
  await page.goto('/register');
  await page.getByLabel(/email/i).fill(email);
  await page.getByLabel(/password/i).fill(password);
  await page.getByLabel(/display name/i).fill(displayName);
  await page.getByRole('button', { name: /register/i }).click();
  await expect(page).toHaveURL('/login');

  // Login
  await page.getByLabel(/email/i).fill(email);
  await page.getByLabel(/password/i).fill(password);
  await page.getByRole('button', { name: /sign in/i }).click();
  await expect(page).toHaveURL('/dashboard');

  return { email, password, displayName };
}

test.describe('Dashboard', () => {
  test('should display dashboard with welcome message', async ({ page }) => {
    const { displayName } = await loginAsNewUser(page);

    await expect(page.getByRole('heading', { name: /dashboard/i })).toBeVisible();
    await expect(page.getByText(displayName)).toBeVisible();
    await expect(page.getByText(/welcome back/i)).toBeVisible();
  });

  test('should display quick actions section', async ({ page }) => {
    await loginAsNewUser(page);

    await expect(page.getByText(/quick actions/i)).toBeVisible();
    await expect(page.getByRole('link', { name: /view family tree/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /manage photos/i })).toBeVisible();
  });

  test('should display statistics section', async ({ page }) => {
    await loginAsNewUser(page);

    await expect(page.getByText(/statistics/i)).toBeVisible();
    await expect(page.getByText(/family members/i)).toBeVisible();
    await expect(page.getByText(/photos/i)).toBeVisible();
    await expect(page.getByText(/events/i)).toBeVisible();
  });

  test('should navigate to tree page from quick actions', async ({ page }) => {
    await loginAsNewUser(page);

    await page.getByRole('link', { name: /view family tree/i }).click();

    await expect(page).toHaveURL('/tree');
    await expect(page.getByText(/family tree/i)).toBeVisible();
  });

  test('should navigate to photos page from quick actions', async ({ page }) => {
    await loginAsNewUser(page);

    await page.getByRole('link', { name: /manage photos/i }).click();

    await expect(page).toHaveURL('/photos');
  });
});
