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
1. Add the ``com.palantir.indexpage:dropwizard-index-page:<VERSION>`` dependency to your project's build.gradle file.
    The most recent version number can be found by looking at the [Releases Page](https://github.com/palantir/dropwizard-index-page/releases).
    The dependencies section should look something like this:
    
    ```
    dependencies {
        // ... unrelated dependencies omitted ...
        compile "com.palantir.indexpage:dropwizard-index-page:<VERSION>"
    }
    ```
2. Ensure the base tag in your ``index.html`` page is set:

    ```
    <base href="{{baseUrl}}">
    ```
3. Have your configuration implement `IndexPageConfigurable`:

    ```
    public final class ExampleApplicationConfiguration extends Configuration implements IndexPageConfigurable {

        private final Optional<String> indexPagePath;

        @JsonCreator
        ExampleConfig(@JsonProperty("indexPagePath") Optional<String> indexPagePath) {
            this.indexPagePath = indexPagePath;
        }

        @Override
        public Optional<String> getIndexPagePath() {
            return this.indexPagePath;
        }
    }
    ```

4. Add the bundle to your application:

    ```
    public class ExampleApplication extends Application<ExampleConfiguration> {

        @Override
        public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
            // the default index page path is "./service/web/index.html" and you override it during the bundle creation
            // or in your application configuration
            bootstrap.addBundle(new IndexPageBundle(ImmutableSet.of("/views/*"));`
        }
    }
    ```

Setting up the project with an IDE
----------------------------------
with Eclipse, import the project and run:

        ./gradlew eclipse

with IntelliJ, import the project and run:

        ./gradlew idea

Contributing
------------
Before working on the code, if you plan to contribute changes, please read the [CONTRIBUTING](CONTRIBUTING.md) document.


License
-------
This project is made available under the
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
