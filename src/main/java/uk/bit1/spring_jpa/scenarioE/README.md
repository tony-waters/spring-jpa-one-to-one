# Variant 5 — Unidirectional shared PK (@MapsId) — Profile depends on Customer, Customer doesn’t reference Profile

Schema idea: profile_e.id PK/FK → customer_e.id
Customer has no profile field at all — you access profile via repository.

Note for tutorial: because CustomerE doesn’t reference ProfileE, 
“create profile” becomes a service/repo operation:

> save CustomerE
> create ProfileE(customer, optIn)
> save ProfileE

That’s the point of the variant: unidirectional + shared identity.

---

Variant 5 Test — Unidirectional shared PK (@MapsId), Customer doesn’t reference Profile

This one teaches a different lesson:

Profile depends on Customer (@MapsId)

But Customer has no field pointing to Profile

So there is no cascade/orphan removal through Customer (in object model)

Creating/removing Profile becomes a repo/service concern