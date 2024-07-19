import com.day.cq.dam.api.Asset

boolean request(HttpServletRequest request) {
    return request.getRequestURI() == "/stubs/random-image"
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    def images = resourceResolver.getResource("/content/dam/stubs/images")
            .listChildren()
            .findAll { it.isResourceType("dam:Asset") }

    images.shuffle()

    def image = images.first()
    def asset = image.adaptTo(Asset.class)

    response.setContentType(asset.getMimeType())
    asset.getOriginal().adaptTo(InputStream.class).withCloseable { input ->
        IOUtils.copy(input, response.getOutputStream())
    }
}
