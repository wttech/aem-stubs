import javax.json.Json
import javax.json.stream.JsonGenerator

boolean request(HttpServletRequest request) {
    return request.getRequestURI() == "/stubs/fake-data"
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8")

    def count = StringUtils.defaultIfBlank(request.getParameter("count"), "100") as int
    def generator = Json.createGenerator(response.getWriter())

    generator.writeStartObject()
            .write("count", count)
            .writeStartArray("result")

    for (int i = 0; i < count; i++) {
        generator.writeStartObject()
                .write("id", i + 1)
                .write("firstName", faker.name().firstName())
                .write("lastName", faker.name().lastName())
                .write("address", faker.address().streetAddress())
                .write("birth", faker.date().birthday().toString())
                .writeEnd()
    }

    generator.writeEnd()
            .writeEnd()
            .close()
}
