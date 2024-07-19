import org.apache.commons.lang3.exception.ExceptionUtils

void fail(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    response.setStatus(500)
    template.render(response, "fail.html", [
            "request": request,
            "response": response,
            "exception": exception,

            "logo": repository.readAsBase64("logo-text.png"),
            "stackTrace": ExceptionUtils.getStackTrace(exception),
            "rootCause": ExceptionUtils.getRootCause(exception),
    ])
}
