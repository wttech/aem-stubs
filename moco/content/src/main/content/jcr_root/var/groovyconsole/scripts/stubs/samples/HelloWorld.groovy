import static com.github.dreamhead.moco.Moco.*

stubs.define "hello-world", { server ->
    server.get(by(uri("/hello-world"))).response("Hello! I am sample stub.")

    server.get(
            and(
                    by(uri("/secured")),
                    eq(query("password"), "secret")
            )
    ).response("Secured endpoint revealed!")
}
