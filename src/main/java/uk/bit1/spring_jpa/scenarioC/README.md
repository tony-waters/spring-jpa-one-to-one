# Variant 3 — Shared Primary Key (@MapsId) — bidirectional
Variant 3 — Shared Primary Key (@MapsId) — bidirectional

Schema idea: profile_c.id is both PK and FK to customer_c.id
This models a truly dependent object: “Profile cannot exist without Customer.”

Customer is Parent, Profile is Child
Profile is Owner and MapsId, Customer is Inverse
Bidirectional

