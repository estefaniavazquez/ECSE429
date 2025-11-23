package general;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

import api.CategoriesApiTest;
import api.ProjectsApiTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Suite.class)
@Suite.SuiteClasses({
 //       CategoriesApiTest.class,
        ProjectsApiTest.class
})

public class AllApiTests {
}
