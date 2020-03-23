package com.itmo.ctd.design.labs.actors.search

/**
 * @author mtereshchuk
 */
abstract class AbstractSearchClient : SearchClient {
    override fun search(query: String): SearchResult {
        val pages = (1..RESULT_SIZE).map {
            PageData(url(query, it), title(query, it), snippet(query, it))
        }
        return SearchResult(pages, name)
    }

    private fun url(query: String, num: Int) = "https://allabout${query.toLowerCase()}$num.com"

    private fun title(query: String, num: Int) = "All about $query ($num)"

    private fun snippet(query: String, num: Int) = "\"${query.capitalize()} ($num)\" is a very interesting thing..."

    companion object {
        private const val RESULT_SIZE = 10
    }
}

class GoogleClient : AbstractSearchClient() {
    override val name = "Google"
}

class YandexClient : AbstractSearchClient() {
    override val name = "Yandex"
}

class MailRuClient : AbstractSearchClient() {
    override val name = "Mail.ru"
}