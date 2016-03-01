[![Circle CI](https://circleci.com/gh/palantir/dropwizard-index-page.svg?style=svg&circle-token=bff5c5b6816da034954a2fd7bb65bee9d6f9c33e)](https://circleci.com/gh/palantir/dropwizard-index-page)
[ ![Download](https://api.bintray.com/packages/palantir/releases/dropwizard-index-page/images/download.svg) ](https://bintray.com/palantir/releases/dropwizard-index-page/_latestVersion)

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
2. Ensure your application configuration implements `IndexPageConfigurable`

3. Add the bundle to your application:

  ```java
  public class ExampleApplication extends Application<ExampleConfiguration> {

      @Override
      public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
          // the default index page path is "index.html"
          bootstrap.addBundle(new IndexPageBundle(ImmutableSet.of("/views/*"));
      }
  }
  ```

License
-------
This project is made available under the
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
