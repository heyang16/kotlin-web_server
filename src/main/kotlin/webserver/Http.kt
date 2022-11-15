package webserver

import java.util.*

// provided files

class Request(val url: String, val authToken: String = "")

class Response(val status: Status, var body: String = "")

typealias HttpHandler = (Request) -> Response

enum class Status(val code: Int) {
  OK(200),
  FORBIDDEN(403),
  NOT_FOUND(404)
}

fun helloHandler(req: Request) : Response {
  // Given a url as a request, return a suitable welcome message

  val rp = Response(Status.OK, "Hello, World") // Default response
  val pm = queryParams(req.url).toMap()

  //Changes the name if specified
  if (pm.containsKey("name")) {
    val name = pm["name"]
    rp.body = "Hello, ".plus(name).plus('!')
  }
  //Changes to upper case if specified
  if (pm.containsKey("style")) {
    if (pm["style"] == "shouting") {
      rp.body = rp.body.uppercase().plus('!')
    }
  }

  return rp
}

fun homepageHandler(req: Request) : Response
  = Response(Status.OK, "This is Imperial.")

fun doCHandler(req: Request) : Response
  = Response(Status.OK, "This is DoC.")

fun notFoundHandler(req: Request) : Response
  = Response(Status.NOT_FOUND)

fun restrictedPageHandler(req: Request): Response
  = Response(Status.OK, "This is very secret.")
//Maps directories to their respective handler functions
val mapping : Map<String, HttpHandler> = mapOf(
  "/" to ::homepageHandler,
  "/computing" to ::doCHandler,
  "/say-hello" to ::helloHandler,
  "/exam-marks" to requireToken("password1", ::restrictedPageHandler)
)

fun configureRoutes(req: Request) : HttpHandler
  = mapping.getOrDefault(path(req.url), ::notFoundHandler)

fun route(req:Request) : Response {
  //Takes in a request and passes it through the matching handler
  //If there is no matching handler, return Status.NOT_FOUND
  val path = path(req.url)

  return if (mapping.containsKey(path)) {
    (mapping[path]!!.invoke(req))
  } else {
    Response(Status.NOT_FOUND)
  }
}

fun requireToken(token: String, wrapped: HttpHandler): HttpHandler {
  fun newHandler(req: Request): Response {
    return if (req.authToken == token) {
      wrapped(req)
    } else {
      Response(Status.FORBIDDEN)
    }
  }
  return ::newHandler
}



