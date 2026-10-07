# Notes

## Summary of changes
1. **Search query (SQL):** AND/OR precedence meant archived tasks appeared in results and the status filter was ignored for title matches. Added brackets. Same fix in `db/queries` and the Oracle package.
2. **Stable ordering:** Added `id DESC` as a tie-breaker so pagination order is consistent.
3. **Artificial delay:** Removed a `Thread.sleep` that slowed short or empty searches by up to 1 second.
4. **Input validation:** Invalid `status`, `page` or `pageSize` now returns 400 instead of 500. Page size is capped at 100.
5. **Loading/error state:** The UI showed "Loading..." forever when a request failed. Fixed, and stale responses are now ignored.
6. **Page reset:** Page now goes back to 1 when search or filter changes.

## What I chose not to change
- Pagination is still done in memory (all rows are loaded, then sliced). Moving to SQL LIMIT/OFFSET is a bigger change than a patch.
- Search debounce, tests and logging cleanup, to keep the diff small and focused.

## Biggest remaining risk
In-memory pagination will become slow as the task table grows. Also, `%` and `_` typed in the search box act as SQL wildcards.

## Assumptions
- Archived tasks should never appear in search results.
- A maximum page size of 100 is reasonable.

## Tools used
I used Claude to help review the code and draft the fixes. I ran the app, reproduced the bugs myself (for example, the status filter showing mixed statuses), applied the changes and tested them. I reviewed every change and can explain it. 
