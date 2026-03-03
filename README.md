# JPA `@OneToOne` Mapping Patterns (Without Join Tables)

This module demonstrates **five common and useful ways** to map a
`@OneToOne` relationship in JPA / Hibernate **without using a join
table**.

Each variant:

-   Produces a **different schema shape**
-   Has a **different owning side**
-   Has a **different lifecycle model**
-   Is backed by a focused `@DataJpaTest` that proves how it actually
    behaves

The domain is intentionally simple:

> A `Customer` may have a `Profile`.

The goal is not business logic, but to show **how mapping choices affect
schema, ownership, cascading, and deletion semantics**.

------------------------------------------------------------------------

## Variant 1 --- FK in Customer table (bidirectional)

**Test:** `CustomerA_OneToOne_DataJpaTest`\
**Entities:** `CustomerA`, `ProfileA`

### Mapping idea

-   Schema: `customer_a.profile_id` → `profile_a.id` (**UNIQUE FK**)
-   The **foreign key lives in the Customer table**
-   The association is **bidirectional**
-   **Customer is the owning side** (has `@JoinColumn`)
-   **Customer is also the lifecycle owner** (`cascade = ALL`,
    `orphanRemoval = true`)

### What this demonstrates

-   Saving a `Customer` also saves its `Profile`
-   Both sides can navigate the relationship
-   Removing the profile reference from `Customer` deletes the `Profile`
    row
-   The database uniqueness constraint enforces **true one-to-one**, not
    many-to-one

This is the most common and straightforward one-to-one mapping.

------------------------------------------------------------------------

## Variant 2 --- FK in Profile table (bidirectional)

**Test:** `CustomerB_OneToOne_DataJpaTest`\
**Entities:** `CustomerB`, `ProfileB`

### Mapping idea

-   Schema: `profile_b.customer_id` → `customer_b.id` (**UNIQUE FK**)
-   The **foreign key lives in the Profile table**
-   The association is **bidirectional**
-   **Profile is the owning side** (has `@JoinColumn`)
-   **Customer is still the lifecycle owner** (cascade + orphan removal
    on `Customer.profile`)

### What this demonstrates

-   Ownership (FK location) and lifecycle ownership are **different
    concerns**
-   Saving a `Customer` still persists the `Profile`
-   The FK is stored on the Profile row instead of the Customer row
-   Orphan removal still works when Customer drops the reference

This variant is useful when the dependent table should carry the FK for
schema or legacy reasons.

------------------------------------------------------------------------

## Variant 3 --- Shared primary key (`@MapsId`) (bidirectional)

**Test:** `CustomerC_MapsId_DataJpaTest`\
**Entities:** `CustomerC`, `ProfileC`

### Mapping idea

-   Schema: `profile_c.id` is **both PK and FK** → `customer_c.id`
-   The Profile **shares the same identity** as the Customer
-   **Profile is the owning side** (it maps the FK via `@MapsId`)
-   **Customer is the lifecycle owner** (cascade + orphan removal)

### What this demonstrates

-   `Profile` **cannot exist without** `Customer`
-   After persistence: `profile.id == customer.id`
-   Deleting the association deletes the dependent row
-   This is the strongest possible "dependent object" modelling in JPA

Use this when the child truly has **no independent identity**.

------------------------------------------------------------------------

## Variant 4 --- Unidirectional FK (Customer → Profile only)

**Test:** `CustomerD_Unidirectional_DataJpaTest`\
**Entities:** `CustomerD`, `ProfileD`

### Mapping idea

-   Schema: `customer_d.profile_id` → `profile_d.id` (**UNIQUE FK**)
-   Only `Customer` has a reference to `Profile`
-   The association is **unidirectional**
-   **Customer is both owning side and lifecycle owner**

### What this demonstrates

-   Same schema shape as Variant 1, but **simpler object model**
-   No need to keep two sides in sync
-   Cascade and orphan removal still works
-   You lose navigation from `Profile` back to `Customer`, by design

This is often a good default if you don't need bidirectional navigation.

------------------------------------------------------------------------

## Variant 5 --- Unidirectional shared PK (`@MapsId`) with no Customer → Profile field

**Test:** `CustomerE_Unidirectional_MapsId_DataJpaTest`\
**Entities:** `CustomerE`, `ProfileE`

### Mapping idea

-   Schema: `profile_e.id` (PK/FK) → `customer_e.id`
-   **Shared primary key** again
-   But **Customer does not reference Profile at all**
-   The association is only navigable from `Profile` → `Customer`
-   There is **no cascade or orphan removal path** from Customer

### What this demonstrates

-   The database still enforces dependency via shared PK
-   But lifecycle must be managed **explicitly in a service or
    repository**
-   You must:
    -   Save Customer first
    -   Then save Profile
    -   Delete Profile before deleting Customer
-   This shows the difference between **schema-level dependency** and
    **object-graph lifecycle ownership**

This variant is useful when you want strict separation in the domain
model but still want shared identity in the database.

------------------------------------------------------------------------

## Why There Is No Join Table Variant Here

A join table can also be used for `@OneToOne`, but:

-   It adds an extra table and join
-   It is rarely the best choice for true one-to-one
-   It's usually only justified for legacy schemas

So it's intentionally excluded here to keep the examples focused on
**the 5 patterns you'll actually use**.

------------------------------------------------------------------------

## How to Explore These

Each variant has:

-   Its own entities
-   Its own repositories
-   A focused `@DataJpaTest` that proves:
    -   Persistence works
    -   Keys are what you expect
    -   Cascade/orphan removal behaves as described
    -   Shared PK behaves differently from FK+UNIQUE

Run:

``` bash
mvn test
```

...and read the tests side-by-side with the mappings. That's where the
real learning happens.

------------------------------------------------------------------------

## Takeaway

There is no single "correct" `@OneToOne` mapping.

You choose based on:

-   Where you want the foreign key
-   Whether the child has its own identity
-   Who owns lifecycle
-   Whether you need bidirectional navigation
-   How explicit you want deletion rules to be

These five variants cover the **practical design space** you'll
encounter in real JPA projects.