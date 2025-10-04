package general;

import categories.DocumentedCategoriesApiTest;
import categories.UndocumentedCategoriesApiTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        DocumentedCategoriesApiTest.class,
        UndocumentedCategoriesApiTest.class,
})
public class AllApiTests {}
