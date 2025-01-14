# Coupons API

This project was created as part of a technical submission for SDE II role in Monk Commerce.

## Overview
This project provides an API for managing and applying various types of coupons to enhance shopping cart functionality. It is designed to support current coupon use cases like cart-wise discounts, product-specific offers, and "Buy X, Get Y" deals while allowing future expansions.

---

## Features

### Supported Coupon Types
1. **Cart-wise Discounts:** Apply a discount to the entire cart if the total value exceeds a specified amount.
2. **Product-specific Discounts:** Provide discounts for selected products only.
3. **Buy X, Get Y (BxGy):** Offer free products when a certain quantity of eligible items is purchased. This can include limits on repetitions and specific product combinations.

### API Endpoints
- **POST /coupons:** Create a new coupon.
- **GET /coupons:** Retrieve a list of all coupons.
- **GET /coupons/{id}:** Fetch details of a specific coupon using its ID.
- **PUT /coupons/{id}:** Update the information for an existing coupon.
- **DELETE /coupons/{id}:** Remove a coupon from the database.
- **POST /applicable-coupons:** Determine which coupons are valid for a provided cart and calculate potential savings.
- **POST /apply-coupon/{id}:** Apply a specific coupon to a cart and return the updated cart with discounted prices.

---

## Getting Started

### Assumptions
- A **cart** consists of a list of products, each having an ID, quantity, and price.
- All monetary amounts are treated in a single currency.
- Coupon thresholds are inclusive (e.g., a threshold of 100 applies to carts valued at exactly 100).

### Installation
1. Clone the repository.
2. Build the application:
   ```bash
   mvn clean install
   ```
3. Check and install required dependencies: Java 17+ & Maven 3.9.6
3. Supports h2 in-memory databse but you can configure the database settings in `application.properties`.
4. Start the application:
   ```bash
   mvn spring-boot:run
   ```

---

## Entity Relationship Map

### Core Entities
```plaintext
ApplicableCoupon
  ├── couponId: long (Unique identifier for the coupon)
  ├── discount: double (Discount amount or percentage)
  └── type: Coupon.CouponType (Type of coupon - cart-wise, product-wise, BxGy)

Cart
  ├── items: List<CartItem> (List of products in the cart)
  ├── totalPrice: Double (Total price of all items before discounts)
  ├── totalDiscount: Double (Total discount applied to the cart)
  └── finalPrice: Double (Final price after applying all discounts)

CartItem
  ├── productId: Long (Product identifier)
  ├── quantity: Integer (Quantity of the product in the cart)
  ├── price: Double (Unit price of the product)
  └── totalDiscount: Double (Discount applied to the specific product)

Coupon
  ├── id: Long (Unique identifier for the coupon)
  ├── type: CouponType (Type of coupon - cart-wise, product-wise, BxGy)
  ├── details: CouponDetails (Embedded object with detailed rules and conditions)
  └── expirationDate: LocalDate (Expiration date of the coupon)

CouponDetails
  ├── threshold: Double (Minimum cart value for the coupon to apply)
  ├── discount: Double (Discount value or percentage)
  ├── productId: Long (Product-specific identifier, if applicable)
  ├── buyProducts: List<ProductDetails> (Products required to activate the coupon)
  ├── getProducts: List<ProductDetails> (Products offered as part of the deal)
  └── repetitionLimit: Integer (Maximum times the deal can be applied)

ProductDetails
  ├── productId: Long (Identifier of the product)
  └── quantity: Integer (Quantity required/offered in the deal)
```

---

### Relationships
- **ApplicableCoupon → Coupon**:
  - `couponId` links to the `id` in the `Coupon` entity, representing the coupon being applied.
  
- **Cart → CartItem**:
  - `items` is a collection of `CartItem` objects representing the products in the cart.
  
- **Coupon → CouponDetails**:
  - Embedded relationship where `CouponDetails` holds conditions and discount logic for the coupon.

- **CouponDetails → ProductDetails**:
  - `buyProducts` and `getProducts` define the "Buy X, Get Y" logic using a list of `ProductDetails`.

#### Not Directly Related Entities
- Placeholder for auxiliary or supporting entities that can be added in the future.

---

## API Documentation

### 1. **POST /coupons**
   - **Description**: Create a new coupon.
   - **Request Body**:
     ```json
     {
        "type": "product-wise",
        "details": {
            "product_id": 1,
            "discount": 20
        },
        "expiration_date": "2025-01-13"
     }
     ```
   - **Response**:
     - **Status**: 201 Created
     - **Body**:
       ```json
       {
            "id": 1,
            "type": "product-wise",
            "details": {
                "discount": 20.0,
                "product_id": 1
            },
            "expiration_date": "2025-01-12"
       }
       ```

### 2. **GET /coupons**
   - **Description**: Retrieve all coupons.
   - **Response**:
     - **Status**: 200 OK
     - **Body**:
       ```json
       [
            {
                "id": 1,
                "type": "product-wise",
                "details": {
                    "discount": 20.0,
                    "product_id": 1,
                    "buy_products": [],
                    "get_products": []
                },
                "expiration_date": "2025-01-12"
            },
            {
                "id": 2,
                "type": "cart-wise",
                "details": {
                    "threshold": 100.0,
                    "discount": 10.0,
                    "buy_products": [],
                    "get_products": []
                },
                "expiration_date": null
            }
        ]
       ```

### 3. **GET /coupons/{id}**
   - **Description**: Retrieve a specific coupon by its ID.
   - **Path Parameter**: 
     - `id` (integer) - The ID of the coupon. For e.g. `2`
   - **Response**:
     - **Status**: 200 OK
     - **Body**:
       ```json
       {
            "id": 2,
            "type": "cart-wise",
            "details": {
                "threshold": 100.0,
                "discount": 10.0,
                "buy_products": [],
                "get_products": []
            },
            "expiration_date": null
       }
       ```

### 4. **PUT /coupons/{id}**
   - **Description**: Update a specific coupon by its ID.
   - **Path Parameter**: 
     - `id` (integer) - The ID of the coupon to update. For e.g. `1`
   - **Request Body**:
     ```json
     {
        "type": "bxgy",
        "details": {
            "buy_products": [
                {
                    "product_id": 1,
                    "quantity": 3
                },
                {
                    "product_id": 2,
                    "quantity": 3
                }
            ],
            "get_products": [
                {
                    "product_id": 3,
                    "quantity": 1
                }
            ],
            "repetition_limit": 2
        }
     }
     ```
   - **Response**:
     - **Status**: 200 OK
     - **Body**:
       ```json
       {
            "id": 1,
            "type": "bxgy",
            "details": {
                "buy_products": [
                    {
                        "quantity": 3,
                        "product_id": 1
                    },
                    {
                        "quantity": 3,
                        "product_id": 2
                    }
                ],
                "get_products": [
                    {
                        "quantity": 1,
                        "product_id": 3
                    }
                ],
                "repetition_limit": 2
            },
            "expiration_date": null
       }
       ```

### 5. **DELETE /coupons/{id}**
   - **Description**: Delete a specific coupon by its ID.
   - **Path Parameter**: 
     - `id` (integer) - The ID of the coupon to delete. For e.g. `1`
   - **Response**:
     - **Status**: 204 No Content

### 6. **POST /applicable-coupons**
   - **Description**: Fetch all applicable coupons for a given cart and calculate the total discount for each coupon.
   - **Request Body**:
     ```json
     {
        "cart": {
            "items": [
                {
                    "product_id": 1,
                    "quantity": 6,
                    "price": 50
                }, // Product X
                {
                    "product_id": 2,
                    "quantity": 6,
                    "price": 30
                }, // Product Y
                {
                    "product_id": 3,
                    "quantity": 2,
                    "price": 25
                } // Product Z
            ]
        }
     }
     ```
   - **Response**:
     - **Status**: 200 OK
     - **Body**:
       ```json
       {
            "applicable_coupons": [
                {
                    "couponId": 2,
                    "discount": 53.0,
                    "type": "cart-wise"
                },
                {
                    "couponId": 3,
                    "discount": 50.0,
                    "type": "bxgy"
                }
            ]
       }
       ```

### 7. **POST /apply-coupon/{id}**
   - **Description**: Apply a specific coupon to the cart and return the updated cart with discounted prices for each item.
   - **Path Parameter**: 
     - `id` (integer) - The ID of the coupon to apply. For e.g. `3`
   - **Request Body**:
     ```json
     {
        "cart": {
            "items": [
                {
                    "product_id": 1,
                    "quantity": 6,
                    "price": 50
                }, // Product X
                {
                    "product_id": 2,
                    "quantity": 6,
                    "price": 30
                }, // Product Y
                {
                    "product_id": 3,
                    "quantity": 2,
                    "price": 25
                } // Product Z
            ]
        }
     }
     ```
   - **Response**:
     - **Status**: 200 OK
     - **Body**:
       ```json
       {
            "updated_cart": {
                "items": [
                    {
                        "product_id": 1,
                        "quantity": 6,
                        "price": 50.0
                    },
                    {
                        "product_id": 2,
                        "quantity": 6,
                        "price": 30.0
                    },
                    {
                        "product_id": 3,
                        "quantity": 4,
                        "price": 25.0,
                        "total_discount": 50.0
                    }
                ],
                "total_price": 580.0,
                "total_discount": 50.0,
                "final_price": 530.0
            }
       }
       ```

### Major Exceptions Handled  

#### **Endpoint: `/applicable-coupons`**  
- **No Applicable Coupons**  
  - **Exception:** `ResponseStatusException` (Status: `HttpStatus.OK`, Message: "No coupon is applicable on this cart.")  
  - **Condition:** When no coupons are applicable to the given cart.  


#### **Endpoint: `/apply-coupon/{couponId}`**  
- **Coupon Expired**  
  - **Exception:** `CouponExpiredException` (Status: `HttpStatus.BAD_REQUEST`)  
  - **Condition:** When the coupon is past its expiration date.  

- **Coupon Not Applicable**  
  - **Exception:** `CouponNotApplicableException` (Status: `HttpStatus.BAD_REQUEST`)  
  - **Condition:** When the coupon doesn't meet the conditions for cart.  

- **Coupon Not Found**  
  - **Exception:** `CouponNotFoundException` (Status: `HttpStatus.BAD_REQUEST`)  
  - **Condition:** When the provided `couponId` is invalid.  

---
---

## BxGy Coupon Logic Explained

### Overview
The **BxGy (Buy X Get Y)** coupon logic allows applying discounts when customers purchase specific quantities of products (buy products). The offer also requires the "get products" to be present in the cart, ensuring all necessary conditions are met before applying the discount.

---

### How It Works

#### Key Rules:
1. **Buy Product Validation**:  
   - All products in the `buy_products` list must be present in the cart with at least the specified quantities or more.  
   - The coupon can be applied multiple times (up to the `repetition_limit`) if the cart satisfies the buy conditions repeatedly.

2. **Get Product Validation**:  
   - All products in the `get_products` list must already exist in the cart with at least one quantity.  
   - Discounts are applied only to the specified "get products."

3. **Discount Calculation**:  
   - The total discount is calculated based on the quantities of the "get products" added by the coupon logic.

---

### Scenario
**Coupon**: Buy 3 Product X and 3 Product Y, Get 1 Product Z (max 2 repetitions).

**Cart Input**:
```json
{
  "items": [
    { "product_id": 1, "quantity": 6, "price": 50 }, // Product X
    { "product_id": 2, "quantity": 4, "price": 30 }, // Product Y
    { "product_id": 3, "quantity": 1, "price": 25 }  // Product Z
  ]
}
```

**Coupon Definition**:
```json
{
  "type": "bxgy",
  "details": {
    "buy_products": [
      { "product_id": 1, "quantity": 2 }, // Product X
      { "product_id": 2, "quantity": 2 }  // Product Y
    ],
    "get_products": [
      { "product_id": 3, "quantity": 1 }  // Product Z
    ],
    "repetition_limit": 3
  }
}
```

**Processing**:
1. Validate `buy_products`:  
   - Product X (6 units) satisfies 3 repetitions (6 ÷ 2 = 3).  
   - Product Y (4 units) satisfies 2 repetitions (4 ÷ 2 = 2).  
   - **Result**: Max 2 repetitions possible due to Product Y, less than the `repetition_limit`.

2. Validate `get_products`:  
   - Product Z exists in the cart with a quantity of 1 (≥ 1).  

3. Apply Discount:  
   - 2 repetitions mean 2x units of Product Z are considered for the discount.
   - **Discount = Price of Product Z × Quantity Discounted** = `25 × 2 = 50`.

**Output**:
```json
{
    "updated_cart": {
        "items": [
            {
                "product_id": 1,
                "quantity": 6,
                "price": 50.0
            },
            {
                "product_id": 2,
                "quantity": 4,
                "price": 30.0
            },
            {
                "product_id": 3,
                "quantity": 3,
                "price": 25.0,
                "total_discount": 50.0
            }
        ],
        "total_price": 495.0,
        "total_discount": 50.0,
        "final_price": 445.0
    }
}
```

## Notes
- The logic ensures all `buy_products` and `get_products` are checked before applying the coupon.  
- Additional discountable "get products" are added to the cart.
- Prices of "get products" are used to calculate the discount.

---

## Future Improvements
1. Add support for applying multiple coupons to a single cart.
3. Improve the efficiency of database queries.

### Proposed Enhancements for BxGy Coupon Logic

1. **Dynamic Buy and Get Products Configuration**  
   - Support flexible configurations like `B2G1`, `B3G1`, or `B4G3` using arrays of `buyProducts` and `getProducts`.  
   - Allow any combination of products from the `buyProducts` array to qualify for discounts on any combination of `getProducts` based on the specified quantities.  
   - For higher `repetition_limit`, distribute discounts dynamically across multiple eligible `getProducts` in the cart.

2. **Maintaining Product Entity**  
   - Introduce a `Product` entity to track product details such as price and availability.  
   - Automatically add `getProducts` to the cart when missing, ensuring users see applicable discounts.  
   - If a `getProduct` is already in the cart (e.g., 1 quantity is present and 2 are free), dynamically add the extra quantity to meet the discount criteria, optimizing savings for the user.

3. **Caching BxGy Coupon Operations**  
   - Precompute and cache eligible products, quantities, and discounts when a user queries `/applicable-coupons`.  
   - Reuse this cached data when the user selects a BxGy coupon, avoiding redundant calculations.  
   - Remove the cached data once the coupon is applied to maintain accuracy and efficiency.

These enhancements ensure flexibility, optimization, and a smoother user experience while reducing computational overhead.

---

## Limitations
- **Complex BxGy Offers:** Currently supports simple "Buy X of A, Get Y of B" logic. More advanced combinations require future enhancements.
- **Large Datasets:** Performance optimizations may be necessary for handling very large coupon datasets or carts.
