boolean request(HttpServletRequest request) {
    return request.getRequestURI() == "/stubs/"
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8")
    response.getWriter().write("""{"message": "Index is here"}""")
}
