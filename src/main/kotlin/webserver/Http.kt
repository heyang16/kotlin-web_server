package webserver

// provided files

class Request(val url: String, val authToken: String = "")

class Response(val status: Status, var body: String = "")

typealias HttpHandler = (Request) -> Response

enum class Status(val code: Int) {
  OK(200),
  FORBIDDEN(403),
  NOT_FOUND(404)
}

fun helloHandler(req: Request): Response {
  // Given a url as a request, return a suitable welcome message

  val rp = Response(Status.OK, "Hello, World!") // Default response
  val pm = queryParams(req.url).toMap()

  // Changes the name if specified
  if (pm.containsKey("name")) {
    val name = pm["name"]
    rp.body = "Hello, $name!"
  }
  // Changes to upper case if specified
  if (pm.containsKey("style")) {
    if (pm["style"] == "shouting") {
      rp.body = rp.body.uppercase()
    }
  }

  return rp
}

fun homepageHandler(req: Request): Response =
  Response(Status.OK, "This is Imperial.")

fun doCHandler(req: Request): Response =
  Response(Status.OK, "This is DoC.")

fun notFoundHandler(req: Request): Response =
  Response(Status.NOT_FOUND)

fun restrictedPageHandler(req: Request): Response =
  Response(Status.OK, "This is very secret.")

// Maps directories to their respective handler functions
val routeMap: Map<String, HttpHandler> = mapOf(
  "/" to ::homepageHandler,
  "/computing" to ::doCHandler,
  "/say-hello" to ::helloHandler,
  "/exam-marks" to requireToken("password1", ::restrictedPageHandler)
)

fun configureRoutes(
  req: Request,
  m: Map<String, HttpHandler> = routeMap
): HttpHandler =

// Matches directory to its handler
  m.getOrDefault(path(req.url), ::notFoundHandler)

fun route(req: Request): Response =
// Takes in a request and passes it through the matching handler
  configureRoutes(req).invoke(req)

fun requireToken(token: String, wrapped: HttpHandler): HttpHandler {
  // Modifies a Handler to require an authenticator token
  fun newHandler(req: Request): Response {
    return if (req.authToken == token) {
      wrapped(req)
    } else {
      Response(Status.FORBIDDEN)
    }
  }
  return ::newHandler
}
