package com.helenayua.webscrap

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.math.BigDecimal

data class Book(
    val url: String,
    val name: String,
    val category: String,
    val priceInclTax: BigDecimal,
    val priceExclTax: BigDecimal,
    val availabilityText: String,
    val stockCount: Int,
    val rating: Int,
    val reviewCount: Int,
    val imageUrl: String,
    val description: String? // null for books with no description
)

fun main() {
    val startTime = System.currentTimeMillis()

    val allUrls = findAllBookUrls(startUrl)
    val urls = limit?.let { allUrls.take(it) } ?: allUrls.toList()

    val books = parseAll(urls)
    writeBooksToJson(books, outputFile)
    printChecks(books)

    val seconds = (System.currentTimeMillis() - startTime) / 1000
    println("Wrote $outputFile. Total time: $seconds seconds")
}

fun parseBook(url: String): Book {
    val doc = Jsoup.connect(url).get()

    val name = doc.select("h1").text()
    val category = doc.select("ul.breadcrumb li").eq(2).text()

    val priceInclTax = toPrice(tableValue(doc, "Price (incl. tax)"))
    val priceExclTax = toPrice(tableValue(doc, "Price (excl. tax)"))
    val availabilityText = tableValue(doc, "Availability")
    val reviewCount = tableValue(doc, "Number of reviews").toInt()

    val stockCount = findNumber(availabilityText)
    val ratingWords = doc.select("div.product_main p.star-rating").attr("class")
    val rating = ratingToNumber(ratingWords)

    val imageUrl = doc.select("div.item.active img").first()?.absUrl("src") ?: ""
    val description = doc.select("#product_description + p").first()?.text()

    return Book(
        url = url,
        name = name,
        category = category,
        priceInclTax = priceInclTax,
        priceExclTax = priceExclTax,
        availabilityText = availabilityText,
        stockCount = stockCount,
        rating = rating,
        reviewCount = reviewCount,
        imageUrl = imageUrl,
        description = description
    )
}

fun tableValue(doc: Document, label: String): String =
    doc.select("table tr").first { it.select("th").text() == label }.select("td").text()

fun toPrice(text: String): BigDecimal =
    BigDecimal(text.replace(Regex("[^0-9.]"), ""))

fun findNumber(text: String): Int {
    val match = Regex("\\d+").find(text)
    return match?.value?.toInt() ?: 0
}

fun ratingToNumber(classes: String): Int = when {
    classes.contains("One") -> 1
    classes.contains("Two") -> 2
    classes.contains("Three") -> 3
    classes.contains("Four") -> 4
    classes.contains("Five") -> 5
    else -> 0
}