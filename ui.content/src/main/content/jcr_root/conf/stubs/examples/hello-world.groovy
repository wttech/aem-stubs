package conf.stubs.example

import HttpServletRequest;
import HttpServletResponse;
import StringUtils;

boolean request(HttpServletRequest request) {
    return true
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    def message = StringUtils.defaultIfBlank(request.getParameter("message"), "Hello, World!")
    response.getWriter().write("""{"message": "$message"}""")
}
