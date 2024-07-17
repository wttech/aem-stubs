import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils

boolean request(HttpServletRequest request) {
    return true
}

void respond(HttpServletRequest request, HttpServletResponse response) {
    def message = StringUtils.defaultIfBlank(request.getParameter("message"), "Hello, World!")
    response.getWriter().write("""{"message": "$message"}""")
}
