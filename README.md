# Nassef'sEgyptianPyramidsApp

A Java 17 command-line ledger for planning gold allocations across 100 global
pyramid projects. This is an in-memory simulation: it does not connect to
financial services, arrange payments, or move physical gold. All data resets
when the application exits.

## Run

From the `demo` directory:

```text
mvn package
java -cp target/classes com.example.Main
```

## Commands

Enter `help` in the running application for the command list. Gold amounts are
in grams and accept up to three decimal places.

```text
1
2
21
contribute P001 500 Alice Example
3
4
1
4
5
fund 2500
locate P001 Giza, Egypt
allocate P001 750.5
status
projects
release P001 50
q
```

Projects are identified as `P001` through `P100`. Funding increases the
simulated treasury; allocation earmarks funds for a project, and release
returns earmarked funds to the treasury. Allocations cannot exceed the
available balance, and releases cannot exceed a project's allocation.

Command `1` (or `pharaohs`) lists the historical pharaoh catalog.
Command `2` prompts for a pharaoh ID from that list and displays the selected
catalog entry's available fields.
Command `3` lists all 100 records from `pyramid.json`, including each pyramid's
ID, name, and contributors. Use
`contribute <project-id> <grams> <contributor name>` to record a simulated
contribution directly for a pyramid; repeated contributions by the same name
are totaled. This ledger resets when the app exits.
Command `4` prompts for a pyramid ID from `pyramid.json` (0 through 99) and
displays that record's ID, name, and contributors.
Command `5` reports pyramids successfully looked up with command `4`, in request
order, with each pyramid listed once even if it was looked up repeatedly.
