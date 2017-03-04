import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.mvc.Results.ok;
import static play.test.Helpers.*;
import static org.junit.Assert.*;

/**
 * JUnit tests that can call all parts of a play app.
 */

public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Please Login or Register to Continue..");
        assertEquals("text/html", html.contentType());
        assertTrue(html.body().contains("Please Login or Register to Continue."));
    }

  /*  @Test
    public void testLoginRoute(){
        Result result = route(fakeRequest("GET", "/login")
                .method(GET);
        assert(contentAsString(result)).equals(200); //contains(200)
    }

  /*  @Test
    public void test() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                String username = "Aerus";
                Result res = route(fakeRequest("GET", "/")
                        .withSession("username", username)
                        .withSession("key","value"));
                assert(contentAsString(res).contains(username));
            }
        });
    }*/
}
