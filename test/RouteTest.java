import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;

import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;

/**
 * Created by eoghan on 05/03/2017.
 */
public class RouteTest {



  /*  @Test
    public void testLoginRoute(){
        Result result = Helpers.route(fakeRequest("GET", "/login")

                //assert(contentAsString(result)).equals(200); //contains(200)
        assert(contentAsString(result)).equals(Helpers.OK);
    }

    @Test
    public void testPage() {
        FakeRequest testRequest = new FakeRequest(Helpers.GET, "/page")
                .withSession("email", "mail@example.com");
        Result result = Helpers.route(testRequest);
        assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
    }*/
}
