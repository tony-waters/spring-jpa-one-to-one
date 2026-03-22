# Spring JPA One-to-One Relationships

A focused exploration of six different ways to model one-to-one relationships in JPA, 
with working examples, tests, and schema assertions.

This repo is not about CRUD. 
It’s about understanding trade-offs, ownership, lifecycle control, and database shape.

------

## What You’ll Learn

- Where to put the foreign key (parent vs child)
- When to use bidirectional vs unidirectional
- How @MapsId shared primary keys actually behave
- What cascade + orphanRemoval really do
- How JPA behaviour differs from database constraints
- Why “clean domain model” vs “explicit lifecycle” is a real trade-off

## Variants Overview

### Variant A — Bidirectional, FK in Parent

- CustomerA owns relationship (@JoinColumn)
- ProfileA is inverse (mappedBy)
- FK stored in customer table

#### Pros

- Simple navigation both ways
- Parent controls lifecycle

#### Cons

- FK in parent is less common
- Inverse side LAZY is often ignored by Hibernate

### Variant B — Bidirectional, FK in Child ⭐ (Recommended Default)

- ProfileB owns relationship
- FK stored in profile table

#### Pros

- Most natural relational model
- Clear ownership
- Good balance of control + simplicity

#### Cons

- Still bidirectional complexity

👉 Start here for most real-world cases

### Variant C — Bidirectional, Shared PK (@MapsId)

- ProfileC.id == CustomerC.id
- Strong composition

#### Pros

- Enforces true 1:1 at DB level
- No separate FK column
- Very tight coupling

### Cons

- Awkward lifecycle
- Harder to replace child
- Bidirectional @MapsId has edge-case complexity

👉 Use when child cannot exist independently

### Variant D — Unidirectional, FK in Parent

- CustomerD → ProfileD
- Profile does not reference Customer

#### Pros

- Very simple model
- Easy lifecycle (parent controls everything)

#### Cons

- No navigation from profile
- FK still in parent

### Variant E — Unidirectional, FK in Child (Explicit Lifecycle)

- ProfileE → CustomerE
- No relationship management in entities

#### Pros

- Very explicit
- No hidden cascade/orphan behaviour
- Clean separation of concerns

#### Cons

- Caller must manage lifecycle manually
- Easier to misuse

👉 Good for service-layer controlled systems

### Variant F — Unidirectional, Shared PK (@MapsId, Explicit Lifecycle)

- ProfileF.id == CustomerF.id
- No back-reference in parent

#### Pros

- Strong composition + simple model
- No bidirectional complexity
- Very explicit lifecycle

#### Cons

- Caller must manage ordering
- Less convenient than cascade model

## Testing Strategy

Each variant is covered by two types of tests:

### 1. Entity Contract Tests

Pure unit tests (no Spring/JPA):

- Prevent duplicate profile creation
- Prevent invalid removal
- Enforce invariants

Example:

``` java
assertThatThrownBy(() -> customer.createProfile(false))
.isInstanceOf(IllegalStateException.class);
```

### 2. JPA / Integration Tests (@DataJpaTest)

- Cascade persist / delete
- Orphan removal
- Replacement behaviour
- DB constraints (FK, UNIQUE)
- Schema assertions via JdbcTemplate

Example:

``` java
assertThat(profileRepository.findById(profileId)).isNotPresent();
```

## Key Insights
### 1. Owning Side Matters More Than You Think

- Only the owning side writes the FK
- mappedBy side is effectively read-only

### 2. orphanRemoval ≠ Cascade Delete

- cascade = REMOVE → delete when parent deleted
- orphanRemoval = true → delete when reference removed

You usually want both.

### 3. Shared PK (@MapsId) Changes Everything

- Child identity is tied to parent
- You cannot treat it like a normal entity
- Replacement becomes “remove + recreate”

### 4. ORM vs Database Failures Are Different

You will see two failure types:

#### ORM-level (Hibernate)

``` java
InvalidDataAccessApiUsageException
```

#### Database-level

``` java
DataIntegrityViolationException
```

The difference depends on:

- managed vs detached state
- lush timing
- constraint enforcement

### 5. Lazy One-to-One Is Not Reliable

Even if you write:

``` java
@OneToOne(fetch = FetchType.LAZY)
```

Hibernate often loads it eagerly unless bytecode enhancement is active.

👉 Treat LAZY one-to-one as best-effort, not guaranteed.
And prove behaviour through tests.

## Recommendations

### Default choice
#### 👉 Variant B (FK in child, bidirectional)

### Strong composition
#### 👉 Variant C or F (@MapsId)

- C = bidirectional convenience
- F = explicit + simpler mental model

### Maximum control / service-driven
#### 👉 Variant E or F

## Tech Stack

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Hibernate 7
- H2 (test database)
- AssertJ

## How to Run

``` terminaloutput
mvn clean test
```

All variants are fully tested with schema + behaviour verification.

## Final Thought

There is no “one correct” way to model a one-to-one.

You are choosing between:

- Convenience vs control
- Implicit lifecycle vs explicit lifecycle
- Flexibility vs strict composition

This repo exists to make those trade-offs visible, testable, and understandable.