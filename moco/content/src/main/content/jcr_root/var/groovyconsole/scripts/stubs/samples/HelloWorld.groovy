import static com.github.dreamhead.moco.Moco.*

moco.with {
    get(by(uri("/hello-world"))).response("Hello! I am sample stub.")

    get(
            and(
                    by(uri("/secured")),
                    eq(query("password"), "secret")
            )
    ).response("Secured endpoint revealed!")
}
