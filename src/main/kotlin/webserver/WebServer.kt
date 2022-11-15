package webserver

// write your web framework code here:

fun scheme(url: String): String = url.substringBefore(':')

fun host(url: String): String = url.substringAfter("://").substringBefore('/')

fun path(url: String): String = url.substringAfter(host(url)).substringBefore('?')

fun queryParams(url: String): List<Pair<String, String>> {
  return when {
    url.contains('?') -> {
      val qstring = url.substringAfter('?')
      qstring.split('&').map {k -> Pair(k.substringBefore('='), k.substringAfter('='))}
    }

    else -> {
      (emptyList())
    }
  }
}

// http handlers for a particular website...

fun homePageHandler(request: Request): Response = Response(Status.OK, "This is Imperial.")
