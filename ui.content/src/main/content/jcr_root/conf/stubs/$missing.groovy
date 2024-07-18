void respond(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(404)
    template.render(response, "/conf/stubs/\$missing.html", [
            "request": request,
            "response": response,
    ])
}
