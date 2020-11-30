stubs.server
        .get(by(uri("/hello-world")))
        .response("Hello! I am sample stub.")

stubs.server
        .get(
                and(
                        by(uri("/secured")),
                        eq(query("password"), "secret")
                )
        )
        .response("Secured endpoint revealed!")

stubs.server
        .get(by(uri("/current-date")))
        .response(template('Today date is "${date}"', "date", suppliedVar({ new Date() })))

stubs.server
        .get(by(uri("/read-itself")))
        .response(repository.readText("./hello-world.stub.groovy"))

stubs.server
        .get(by(uri("/read_query_file")))
        .response(jcr(template('/conf/stubs/moco/samples/${req.queries["id"]}.json')))

