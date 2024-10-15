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

		// 1. Give me the age of "f0"
		response.getWriter().print("<h2>Give me the age of 'f0'</h2>");
		try {
			Entity e = datastore.get(KeyFactory.createKey("Friend", "f0"));
			response.getWriter().print("<li>Age of f0: " + e.getProperty("age"));
		} catch (EntityNotFoundException e1) {
			response.getWriter().print("<li>f0 not found.");
		}

		// 2. Give me at most 5 users with age 30
		response.getWriter().print("<h2>Give me at most 5 users with age 30</h2>");
		Query ageQuery = new Query("Friend").setFilter(new FilterPredicate("age", FilterOperator.EQUAL, 30));
		List<Entity> ageResult = datastore.prepare(ageQuery).asList(FetchOptions.Builder.withLimit(5));
		response.getWriter().print("<li>Users with age 30 (max 5):<br>");
		for (Entity entity : ageResult) {
			response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName"));
		}

		// 3. Give me all users whose name starts with "f14"
		response.getWriter().print("<h2>Give me all users whose name starts with 'f14'</h2>");
		Query nameQuery = new Query("Friend").setFilter(new FilterPredicate("firstName", FilterOperator.GREATER_THAN_OR_EQUAL, "f14"))
				.setFilter(new FilterPredicate("firstName", FilterOperator.LESS_THAN, "f15"));
		List<Entity> nameResult = datastore.prepare(nameQuery).asList(FetchOptions.Builder.withDefaults());
		response.getWriter().print("<li>Users with name starting with 'f14':<br>");
		for (Entity entity : nameResult) {
			response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName"));
		}

		// 4. Give me all users that are friends with "f0"
		response.getWriter().print("<h2>Give me all users that are friends with 'f0'</h2>");
		Query friendsQuery = new Query("Friend").setFilter(new FilterPredicate("friends", FilterOperator.EQUAL, "f0"));
		List<Entity> friendsResult = datastore.prepare(friendsQuery).asList(FetchOptions.Builder.withDefaults());
		response.getWriter().print("<li>Friends of 'f0':<br>");
		for (Entity entity : friendsResult) {
			response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName"));
		}

		// 5. Give me all users that are friends with "f0" with age = 10
		response.getWriter().print("<h2>Give me all users that are friends with 'f0' with age 10</h2>");
		Query friendsWithAgeQuery = new Query("Friend")
				.setFilter(CompositeFilterOperator.and(
						new FilterPredicate("friends", FilterOperator.EQUAL, "f0"),
						new FilterPredicate("age", FilterOperator.EQUAL, 10)
				));
		List<Entity> friendsWithAgeResult = datastore.prepare(friendsWithAgeQuery).asList(FetchOptions.Builder.withDefaults());
		response.getWriter().print("<li>Friends of 'f0' with age 10:<br>");
		for (Entity entity : friendsWithAgeResult) {
			response.getWriter().print("<li>" + entity.getProperty("firstName") + " " + entity.getProperty("lastName"));
		}
	}
}

