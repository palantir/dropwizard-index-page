Dropwizard Index Page
=====================

A bundle that serves the *index* page for a single page application. It covers the following areas:

- Serves the index page for a list of url patterns specified during the bundle creation.
- Templates the index page with the correct base tag value, the context path of your application.
- Add disable caching header when serving the index page.
  - `cache-control: no-cache, no-store, max-age=0, must-revalidate`


Usage
-----
1. Ensure the base tag in your index page is set:
  ```
  <base href="{{baseUrl}}">
  ```
2. Add the bundle to your application:

  ```java
  public class ExampleApplication extends Application<ExampleConfiguration> {

      @Override
      public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
          // the default index page path is "index.html"
          bootstrap.addBundle(new IndexPageBundle(ImmutableSet.of("/views/*"));
      }
  }
  ```

Advanced
--------
You can also specify the index page path when you add the bundle to your application:

  ```java
  public class ExampleApplication extends Application<ExampleConfiguration> {

      @Override
      public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
          bootstrap.addBundle(new IndexPageBundle("/assets/index.html", ImmutableSet.of("/views/*"));
      }
  }
  ```

License
-------
This project is made available under the
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
