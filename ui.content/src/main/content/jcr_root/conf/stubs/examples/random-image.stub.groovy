import com.day.cq.dam.api.Asset

boolean request(HttpServletRequest request) {
    return request.getRequestURI() == "/stubs/random-image"
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    def imagesDir = resourceResolver.getResource(StringUtils.defaultIfBlank(request.getParameter("dir"), "/content/dam/stubs/images"))
    if (!imagesDir) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND)
        return
    }

    def images = imagesDir.listChildren().findAll { it.isResourceType("dam:Asset") }
    if (images.isEmpty()) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND)
        return
    }

    images.shuffle()

    def image = images.first()
    def asset = image.adaptTo(Asset.class)

    response.setContentType(asset.getMimeType())
    asset.getOriginal().adaptTo(InputStream.class).withCloseable { input ->
        IOUtils.copy(input, response.getOutputStream())
    }
}
