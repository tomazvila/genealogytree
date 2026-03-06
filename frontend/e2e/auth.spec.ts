import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test.describe('Registration', () => {
    test('should show registration form', async ({ page }) => {
      await page.goto('/register');

      await expect(page.getByRole('heading', { name: /create account/i })).toBeVisible();
      await expect(page.getByLabel(/email/i)).toBeVisible();
      await expect(page.getByLabel(/password/i)).toBeVisible();
      await expect(page.getByLabel(/display name/i)).toBeVisible();
      await expect(page.getByRole('button', { name: /register/i })).toBeVisible();
    });

    test('should show validation errors for empty form', async ({ page }) => {
      await page.goto('/register');

      await page.getByRole('button', { name: /register/i }).click();

      await expect(page.getByText(/email is required/i)).toBeVisible();
      await expect(page.getByText(/password is required/i)).toBeVisible();
    });

    test('should show error for invalid email', async ({ page }) => {
      await page.goto('/register');

      await page.getByLabel(/email/i).fill('invalid-email');
      await page.getByLabel(/password/i).fill('SecurePass123!');
      await page.getByLabel(/display name/i).fill('Test User');
      await page.getByRole('button', { name: /register/i }).click();

      await expect(page.getByText(/invalid email/i)).toBeVisible();
    });

    test('should register successfully and redirect to login', async ({ page }) => {
      const uniqueEmail = `test-${Date.now()}@example.com`;

      await page.goto('/register');

      await page.getByLabel(/email/i).fill(uniqueEmail);
      await page.getByLabel(/password/i).fill('SecurePass123!');
      await page.getByLabel(/display name/i).fill('Test User');
      await page.getByRole('button', { name: /register/i }).click();

      // Should redirect to login with success message
      await expect(page).toHaveURL('/login');
      await expect(page.getByText(/registration successful/i)).toBeVisible();
    });

    test('should show error for duplicate email', async ({ page }) => {
      // First register
      const email = `duplicate-${Date.now()}@example.com`;

      await page.goto('/register');
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill('SecurePass123!');
      await page.getByLabel(/display name/i).fill('First User');
      await page.getByRole('button', { name: /register/i }).click();
      await expect(page).toHaveURL('/login');

      // Try to register again with same email
      await page.goto('/register');
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill('SecurePass123!');
      await page.getByLabel(/display name/i).fill('Second User');
      await page.getByRole('button', { name: /register/i }).click();

      await expect(page.getByText(/email already exists/i)).toBeVisible();
    });

    test('should have link to login page', async ({ page }) => {
      await page.goto('/register');

      await page.getByRole('link', { name: /sign in/i }).click();

      await expect(page).toHaveURL('/login');
    });
  });

  test.describe('Login', () => {
    test('should show login form', async ({ page }) => {
      await page.goto('/login');

      await expect(page.getByRole('heading', { name: /sign in/i })).toBeVisible();
      await expect(page.getByLabel(/email/i)).toBeVisible();
      await expect(page.getByLabel(/password/i)).toBeVisible();
      await expect(page.getByRole('button', { name: /sign in/i })).toBeVisible();
    });

    test('should show validation errors for empty form', async ({ page }) => {
      await page.goto('/login');

      await page.getByRole('button', { name: /sign in/i }).click();

      await expect(page.getByText(/email is required/i)).toBeVisible();
      await expect(page.getByText(/password is required/i)).toBeVisible();
    });

    test('should show error for invalid credentials', async ({ page }) => {
      await page.goto('/login');

      await page.getByLabel(/email/i).fill('nonexistent@example.com');
      await page.getByLabel(/password/i).fill('wrongpassword');
      await page.getByRole('button', { name: /sign in/i }).click();

      await expect(page.getByText(/invalid credentials/i)).toBeVisible();
    });

    test('should login successfully and redirect to dashboard', async ({ page }) => {
      // First register a user
      const email = `login-test-${Date.now()}@example.com`;
      const password = 'SecurePass123!';

      await page.goto('/register');
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill(password);
      await page.getByLabel(/display name/i).fill('Login Test User');
      await page.getByRole('button', { name: /register/i }).click();
      await expect(page).toHaveURL('/login');

      // Now login
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill(password);
      await page.getByRole('button', { name: /sign in/i }).click();

      // Should redirect to dashboard
      await expect(page).toHaveURL('/dashboard');
      await expect(page.getByText(/dashboard/i)).toBeVisible();
    });

    test('should have link to register page', async ({ page }) => {
      await page.goto('/login');

      await page.getByRole('link', { name: /create account/i }).click();

      await expect(page).toHaveURL('/register');
    });
  });

  test.describe('Protected Routes', () => {
    test('should redirect to login when accessing dashboard without auth', async ({ page }) => {
      await page.goto('/dashboard');

      await expect(page).toHaveURL('/login');
    });

    test('should redirect to login when accessing tree page without auth', async ({ page }) => {
      await page.goto('/tree');

      await expect(page).toHaveURL('/login');
    });
  });

  test.describe('Logout', () => {
    test('should logout and redirect to home', async ({ page }) => {
      // First login
      const email = `logout-test-${Date.now()}@example.com`;
      const password = 'SecurePass123!';

      await page.goto('/register');
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill(password);
      await page.getByLabel(/display name/i).fill('Logout Test User');
      await page.getByRole('button', { name: /register/i }).click();

      await page.goto('/login');
      await page.getByLabel(/email/i).fill(email);
      await page.getByLabel(/password/i).fill(password);
      await page.getByRole('button', { name: /sign in/i }).click();
      await expect(page).toHaveURL('/dashboard');

      // Now logout
      await page.getByRole('button', { name: /logout/i }).click();

      await expect(page).toHaveURL('/');
    });
  });
});
