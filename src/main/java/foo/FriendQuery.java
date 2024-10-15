package foo;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet(name = "FriendQuery", urlPatterns = { "/query" })
public class FriendQuery extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        response.getWriter().print("<h1> Friends Queries </h1>");

        // 1. Give me the age of “f0”
        response.getWriter().print("<h2> Age of 'f0'</h2>");
        try {
            Entity f0 = datastore.get(KeyFactory.createKey("Friend", "f0"));
            response.getWriter().print("<li>Age of 'f0': " + f0.getProperty("age") + "</li>");
        } catch (EntityNotFoundException e1) {
            response.getWriter().print("<li>'f0' not found.</li>");
        }

        // 2. Give me at most 5 users with age 30
        response.getWriter().print("<h2> At most 5 users with age 30</h2>");
        Query q = new Query("Friend").setFilter(new FilterPredicate("age", FilterOperator.EQUAL, 30));
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(5));
        response.getWriter().print("<li>Number of users found: " + result.size() + "</li>");
        for (Entity entity : result) {
            response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName")
                    + " (Age: " + entity.getProperty("age") + ")</li>");
        }

        // 3. Give me all users whose name starts with “f14”
        response.getWriter().print("<h2> Users whose first name starts with 'f14'</h2>");
        q = new Query("Friend")
                .setFilter(CompositeFilterOperator.and(
                        new FilterPredicate("firstName", FilterOperator.GREATER_THAN_OR_EQUAL, "f14"),
                        new FilterPredicate("firstName", FilterOperator.LESS_THAN, "f15")));
        pq = datastore.prepare(q);
        result = pq.asList(FetchOptions.Builder.withDefaults());
        response.getWriter().print("<li>Number of users found: " + result.size() + "</li>");
        for (Entity entity : result) {
            response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName")
                    + "</li>");
        }

        // 4. Give me all users that are friends with “f0”
        response.getWriter().print("<h2> Users that are friends with 'f0'</h2>");
        q = new Query("Friend").setFilter(new FilterPredicate("friends", FilterOperator.EQUAL, "f0"));
        pq = datastore.prepare(q);
        result = pq.asList(FetchOptions.Builder.withDefaults());
        response.getWriter().print("<li>Number of users found: " + result.size() + "</li>");
        for (Entity entity : result) {
            response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName")
                    + "</li>");
        }

        // 5. Give me all users that are friends with “f0” and have age = 10
        response.getWriter().print("<h2> Users that are friends with 'f0' and have age 10</h2>");
        q = new Query("Friend").setFilter(CompositeFilterOperator.and(
                new FilterPredicate("friends", FilterOperator.EQUAL, "f0"),
                new FilterPredicate("age", FilterOperator.EQUAL, 10)));
        pq = datastore.prepare(q);
        result = pq.asList(FetchOptions.Builder.withDefaults());
        response.getWriter().print("<li>Number of users found: " + result.size() + "</li>");
        for (Entity entity : result) {
            response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName")
                    + "</li>");
        }
    }
}
