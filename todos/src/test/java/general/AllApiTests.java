package general;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

import todos.DocumentedTodosApiTest;
import todos.DocumentedTodosIdApiTest;
import todos.UndocumentedTodosApiTest;
import todos.UndocumentedTodosIdApiTest;

// runs all todo tests together
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Suite.class)
@Suite.SuiteClasses({
    DocumentedTodosApiTest.class,
    UndocumentedTodosApiTest.class,
    DocumentedTodosIdApiTest.class,
    UndocumentedTodosIdApiTest.class
})
public class AllApiTests {
    // nothing here just runner
}
