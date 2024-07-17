// TODO render more sophisticated 404 from HTML gstring template
void respond(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8")
    response.getWriter().write(gson.toJson([
            "message": "Missing!",
    ]))
}
