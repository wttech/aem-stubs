void respond(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(404)
    template.render(response, "missing.html", [
            "request": request,
            "response": response,

            "logo": repository.readAsBase64("logo-text.png"),
    ])
}
