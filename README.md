# 6 Spring JPA One-to-One Relationships

A focused exploration of six different ways to model one-to-one relationships in JPA, 
with working examples, tests, and schema assertions.

This repo is not about CRUD. 
It’s about understanding trade-offs, ownership, lifecycle control, and database shape.

See article: https://tony-waters.github.io/2026/02/25/6-ways-to-map-a-jpa-one-to-one-relationship.html

------

## What it Shows

- Where to put the foreign key (parent vs child)
- When to use bidirectional vs unidirectional
- How @MapsId shared primary keys actually behave
- What cascade + orphanRemoval really do
- How JPA behaviour differs from database constraints

------

## Tech Stack

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Hibernate 7
- H2 (test database)
- AssertJ

------
