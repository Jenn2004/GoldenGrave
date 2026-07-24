# Golden Grove Wines — Cursor Build Spec

Paste the relevant section into Cursor's chat when you start each part. Don't paste
the whole thing at once and ask for everything — build it in the stage order at
the bottom. Keep this file in your repo root (e.g. `SPEC.md`) so you can reference
it in every prompt with `@SPEC.md`.

---

## 1. Project overview (paste this first, once)

```
I'm building "Golden Grove Wines," an e-commerce site for a small homemade wine
business. Brand feel: natural, luxury, fresh, made-with-love, healthy, tasty —
heritage/traditional, not flashy or corporate.

Stack:
- Frontend: React (Vite) + Tailwind CSS
- Backend: Spring Boot (Java), REST API
- Database: MySQL

Set up two folders in this repo: /frontend and /backend, each with their own
package/build files. Don't merge them into one project.
```

## 2. Design tokens (paste every time you ask for a new page/component)

```
Use this exact design system for all UI — don't substitute your own defaults:

Colors:
- Cream (background): #F7F1E6
- Maroon (primary/wine): #5C1A2E
- Maroon deep (hover/dark): #3D0F1C
- Gold (accent): #B08D57, saturated accent #C9932E
- Forest green (secondary accent): #3F4B3B / #4B5A42
- Terracotta (accent): #C1652F
- Blush (soft card bg): #EFD9D3
- Charcoal (text/dark sections): #2B2420

Fonts (Google Fonts):
- Display/headings: Fraunces (use italic weight for emphasis words)
- Body: Work Sans
- Labels/data (ABV%, vintage year, prices): Space Mono, uppercase, letter-spacing

Layout conventions:
- Sticky pill-shaped nav (rounded-full), cream background, maroon text
- Rounded-2xl / rounded-full corners throughout, no sharp corners
- Section backgrounds alternate: cream → gold/blush gradient → maroon or forest
  green (don't repeat the same dark color in two sections back to back)
- Wine/product cards get a color tint matching wine type (red=maroon, white=gold,
  rosé=terracotta/blush, aged=forest/charcoal)
- Trust badges use a wax-seal shape (scalloped circle), not plain icons or stars

I have a reference Home page component already built — I'll paste it below /
it's at src/pages/Home.jsx. Match its exact look and conventions.
```

*(Then paste the actual `Home.jsx` code as reference context, or attach the file
in Cursor with `@Home.jsx`.)*

## 3. Pages needed (for planning, not all in one prompt)

- Home
- Products / Shop (grid, filters by wine type)
- Product Detail (tasting notes, ABV, price, add to cart)
- Cart page
- Login / Signup
- Checkout / Payment
- About Us
- Our Process
- Contact Us
- Reviews
- Admin Dashboard (products, orders, inventory — admin-only route)

## 4. Data model (paste when starting backend)

```
Design a MySQL schema and matching Spring Boot JPA entities for:

- users (id, name, email, password_hash, role [CUSTOMER/ADMIN], created_at)
- products (id, name, type [RED/WHITE/ROSE/AGED], vintage_year, abv,
  price, description, stock_quantity, image_url)
- orders (id, user_id, status [PENDING/PAID/SHIPPED], total_amount, created_at)
- order_items (id, order_id, product_id, quantity, unit_price)
- cart_items (id, user_id, product_id, quantity)
- reviews (id, product_id, user_id, rating, comment, created_at)

Generate the entities, repositories (Spring Data JPA), and a schema.sql /
Flyway migration. Use sensible constraints (e.g. rating 1-5, stock >= 0).
```

## 5. API contract (paste when connecting frontend to backend)

```
Build REST endpoints in Spring Boot for:

- GET /api/products, GET /api/products/{id}
- POST /api/auth/register, POST /api/auth/login (JWT-based auth)
- GET /api/cart, POST /api/cart, DELETE /api/cart/{itemId}
- POST /api/orders (checkout), GET /api/orders (user's order history)
- GET /api/reviews/{productId}, POST /api/reviews
- Admin-only (require ADMIN role): POST/PUT/DELETE /api/products,
  GET /api/admin/orders

Return proper HTTP status codes and JSON error responses. Use Spring Security
with JWT for auth, and role-based access control for admin routes.
```

## 6. Build order (don't skip steps or ask for everything at once)

1. Frontend: scaffold Vite + Tailwind, paste Home.jsx, confirm it renders correctly
2. Frontend: build Products, Product Detail, About, Process, Contact, Reviews
   (static/mock data first — no backend yet)
3. Backend: schema + entities + repositories (section 4)
4. Backend: auth (register/login/JWT)
5. Backend: product + cart + order + review endpoints (section 5)
6. Frontend: connect Products/Cart/Checkout pages to the real API (replace mock data)
7. Frontend: Login/Signup pages wired to auth endpoints
8. Admin Dashboard: protected route, CRUD UI for products, view orders
9. Payment integration (decide provider — e.g. Stripe/Razorpay test mode — before
   asking Cursor to wire it up)
10. Polish pass: loading states, error states, mobile responsiveness

---

### Tips for prompting Cursor as you go
- Always reference the spec file (`@SPEC.md`) or a built page (`@Home.jsx`) so it
  doesn't invent a different style each time.
- Ask for one page or one backend layer per prompt, not "build the whole app."
- After each generation, tell it explicitly if something drifted from the design
  tokens — it'll correct faster with a specific correction than a vague one.
- For the database, ask it to generate the schema file first and review it
  yourself before letting it wire up the entities — easier to fix a wrong
  column type early than after five endpoints depend on it.
