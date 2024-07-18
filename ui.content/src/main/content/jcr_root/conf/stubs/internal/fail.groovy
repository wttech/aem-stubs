import org.apache.commons.lang3.exception.ExceptionUtils

void fail(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    response.setStatus(500)
    template.render(response, "/conf/stubs/internal/fail.html", [
            "request": request,
            "response": response,
            "exception": exception,

            "stackTrace": ExceptionUtils.getStackTrace(exception),
            "rootCause": ExceptionUtils.getRootCause(exception),
    ])
}
