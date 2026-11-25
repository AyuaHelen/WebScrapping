package com.helenayua.webscrap

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tools.jackson.databind.SerializationFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File

// ---- Config: change these with environment variables, no code edits needed ----
val startUrl = System.getenv("CRAWL_START_URL") ?: "https://books.toscrape.com/catalogue/page-1.html"
val delayMs = System.getenv("CRAWL_DELAY_MS")?.toLong() ?: 200L   // pause between books
val limit = System.getenv("CRAWL_LIMIT")?.toInt()                  // empty = crawl everything
val outputFile = System.getenv("CRAWL_OUTPUT") ?: "books.json"

// Takes one listing page and returns the full address of every book on it
fun bookLinksOnPage(listing: Document): List<String> {
    return listing.select("article.product_pod h3 a").map { it.absUrl("href") }
}

// Starts at one listing page and keeps clicking "next" until there is none
fun findAllBookUrls(startUrl: String): Set<String> {
    val urls = LinkedHashSet<String>()
    var pageUrl: String? = startUrl

    while (pageUrl != null) {
        val doc = Jsoup.connect(pageUrl).get()
        urls.addAll(bookLinksOnPage(doc))
        println("Found ${urls.size} book addresses so far")
        pageUrl = doc.select("li.next a").first()?.absUrl("href")
    }
    return urls
}

// Runs parseBook (from M0) on every address and collects the results
fun parseAll(urls: Collection<String>): List<Book> {
    val books = mutableListOf<Book>()
    for ((index, url) in urls.withIndex()) {
        books.add(parseBook(url))
        println("Parsed ${index + 1}/${urls.size}")
        Thread.sleep(delayMs)
    }
    return books
}

// Writes the list of books to a JSON file
fun writeBooksToJson(books: List<Book>, path: String) {
    val mapper = jacksonMapperBuilder()
        .enable(SerializationFeature.INDENT_OUTPUT)   // makes the file readable
        .build()
    mapper.writeValue(File(path), books)
}

// Prints the numbers you need to prove the M1 criteria
fun printChecks(books: List<Book>) {
    println("Books: ${books.size}")
    println("Distinct URLs: ${books.map { it.url }.distinct().size}")
    println("Books per category:")
    books.groupingBy { it.category }.eachCount().toSortedMap()
        .forEach { (category, count) -> println("  $category: $count") }
}