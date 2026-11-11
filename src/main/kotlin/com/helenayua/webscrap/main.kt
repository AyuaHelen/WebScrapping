package com.helenayua.webscrap

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.math.BigDecimal

data class Book(
    val name: String,
    val category: String,
    val priceInclTax: BigDecimal,
    val priceExclTax: BigDecimal,
    val availabilityText: String,
    val stockCount: Int,
    val rating: Int,
    val reviewCount: Int,
    val imageUrl: String,
    val description: String? // Nullable for books with no description
)

fun main() {
    print("Enter a book URL: ")
    val url = readln()
    val book = parseBook(url)

    println("Name: ${book.name}")
    println("Category: ${book.category}")
    println("Price incl. tax: ${book.priceInclTax}")
    println("Price excl. tax: ${book.priceExclTax}")
    println("Availability: ${book.availabilityText}")
    println("Stock count: ${book.stockCount}")
    println("Rating: ${book.rating}")
    println("Review count: ${book.reviewCount}")
    println("Image URL: ${book.imageUrl}")
    println("Description: ${book.description}")
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
    val ratingWords = doc.select("p.star-rating").attr("class")
    val rating = ratingToNumber(ratingWords)

    val imageUrl = doc.select("div.item.active img").first()?.absUrl("src") ?: ""
    val description = doc.select("#product_description + p").first()?.text()

    return Book(
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


