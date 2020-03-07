import static com.github.dreamhead.moco.Moco.*

moco.request(by(uri("/hello-world"))).response("hello world")
