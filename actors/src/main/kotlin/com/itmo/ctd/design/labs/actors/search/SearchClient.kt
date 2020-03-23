package com.itmo.ctd.design.labs.actors.search

/**
 * @author mtereshchuk
 */
interface SearchClient {
    val name: String

    fun search(query: String): SearchResult
}

data class SearchResult(val pages: List<PageData>, val clientName: String) {
    fun view(): String {
        return "$clientName:\n" + pages.joinToString(separator = "\n") { it.view() }
    }
}

data class PageData(val url: String, val title: String, val snippet: String) {
    fun view(): String {
        return "\tURL: $url\n" +
                "\tTitle: $title\n" +
                "\tSnippet: $snippet\n"
    }
}