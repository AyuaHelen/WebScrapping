# Book Crawler (M0: parse one book page)

Takes one book page URL from books.toscrape.com and prints the book's fields.

## Requirements
- JDK (Java Development Kit) 17
- IntelliJ IDEA
- Internet connection

## How to run
1. Open the project folder in IntelliJ and wait for Gradle (build tool) to finish syncing.
2. Go to Settings > Build, Execution, Deployment > Build Tools > Gradle and set "Build and run using" to IntelliJ IDEA. (This lets you type into the Run panel.)
3. Open `main.kt` and click the green play button next to `fun main()`.
4. When it asks "Enter a book URL:", paste a book address, e.g.
   https://books.toscrape.com/catalogue/a-light-in-the-attic_1000/index.html
5. The fields print in the Run panel.

## Where each field comes from in the HTML
| Field | Where on the page |
|---|---|
| name | the `h1` heading |
| category | 3rd item of `ul.breadcrumb` |
| prices, availability, review count | the "Product Information" table, found by row label |
| stock count | the number inside the availability text |
| rating | the class on `p.star-rating` inside `div.product_main` |
| image URL | `src` of the image in `div.item.active` |
| description | the `p` right after `#product_description` (null if missing) |

## Why Jsoup
(Write 2 or 3 sentences in your own words.)