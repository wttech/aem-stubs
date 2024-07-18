void fail(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    response.setStatus(500)
    template.render(response, "/conf/stubs/\$fail.html", [
            "request": request,
            "response": response,
            "exception": exception,
    ])
}
