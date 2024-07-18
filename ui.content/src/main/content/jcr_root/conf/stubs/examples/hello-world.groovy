boolean request(HttpServletRequest request) {
    return request.getRequestURI() == "/stubs/hello-world"
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    def message = StringUti ls.defaultIfBlank(request.getParameter("message"), "Hello, World!")
    response.getWriter().write("""{"message": "$message"}""")
}
