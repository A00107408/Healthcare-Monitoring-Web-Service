import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import controllers.routes;
import javafx.util.Callback;
import org.junit.*;

import play.api.Application;
import play.api.Configuration;
import play.api.Environment;
import play.api.GlobalSettings;
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


import org.junit.Test;

import static play.test.Helpers.contentAsString;

/**
 * JUnit tests that can call all parts of a play app.
 */

public class ApplicationTest {

   // Application fakeApp = (Application) Helpers.fakeApplication();

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render();//("Please Login or Register to Continue.");
        assertEquals("text/html", html.contentType());
        assertTrue(html.body().contains("Please Login or Register to Continue."));
    }
/*
    @Test
    public void badRoute() {

        Result result = Helpers.route(fakeRequest(GET, "/bad"));
        assert(contentAsString(result)).equals(null);
    }

 /*   @Test
    public void indexTemplate() {
        Content html = views.html.index.render("test");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("test");
    }


  /*  @Test
    public void runInBrowser() {
        running(testServer(), HTMLUNIT, browser -> {
            browser.goTo("/");
           // assertEquals("Please Login or Register to Continue.", browser.$("#title").getText());
            browser.$("a").click();
          //  assertEquals("/login", browser.url());
            assertNotNull(browser.$("title").getText());
        });
    }*/


  /*  Application fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
        @Override
        public void onStart(Application app) {
            System.out.println("Starting FakeApplication");
        }
    });



  /*  @Test
    public void testUser() {

        User user = new User(1, "John", 23, "Johnny Utah", "qwerty");
        assertThat(user.age());
    }

    @Test
    public void callIndex(){
        Result result = callAction(routes.UserController.index());
        assertThat(status(result)).isEqualTo(OK);
    }

    @Test
    public void indexTemplate(){
        Content html = views.html.index.render("test");
        assertThat(contentType(html)).isEqualTo("text\html");
        assertThat(contentAsString(html)).contains("TEST");
    }*/





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
