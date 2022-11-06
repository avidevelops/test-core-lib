package in.av.testcorelib.extensions;

import in.av.testcorelib.annotations.CreateTestIssue;
import in.av.testcorelib.annotations.IssueKey;
import in.av.testcorelib.persistence.ReferencesPersister;
import in.av.testcorelib.persistence.ZephyrReferencesPersister;
import in.av.testcorelib.persistence.mapping.Failure;
import in.av.testcorelib.persistence.mapping.Skipped;
import in.av.testcorelib.persistence.mapping.TestCase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * This extension should be used in test classes which contain references to test issues
 * and need to create special reports for further test status update via Zephy API
 */
public class ZephyrReferencesExtension implements BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        BeforeAllCallback,
        AfterAllCallback,
        TestWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZephyrReferencesExtension.class.getSimpleName());
    private static final String SUREFIRE_REPORTS_FOLDER = "target" + System.getProperty("file.separator") + "zephyr" + System.getProperty("file.separator");

    public final List<TestCase> testCases = new ArrayList<>();

    private double testStarted;

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        LOGGER.info(">>>>> [TEST COMPLETED]: " + context.getRequiredTestClass().getName());
        LOGGER.info("[=============================================================================================]");
        if (!testCases.isEmpty()) {
            ReferencesPersister persister = new ZephyrReferencesPersister(SUREFIRE_REPORTS_FOLDER);
            persister.persist(testCases);
        }
        context.getStore(ExtensionContext.Namespace.GLOBAL).put(
                context.getRequiredTestClass().getName(), testCases
        );
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        LOGGER.info("[=============================================================================================]");
        LOGGER.info(">>>>> [TEST STARTED]: " + context.getRequiredTestClass().getName());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method ->
                        method.getDeclaredAnnotation(BeforeAll.class) == null
                        && method.getDeclaredAnnotation(BeforeEach.class) == null
                        && method.getDeclaredAnnotation(AfterAll.class) == null
                        && method.getDeclaredAnnotation(AfterEach.class) ==  null
                )
                .forEach(this::addTestCase);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        LOGGER.info(">>>>> [TEST STARTED]: " + context.getRequiredTestMethod().getName());
        testStarted = System.currentTimeMillis();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        if (context.getRequiredTestClass().getAnnotation(Disabled.class) != null)
            LOGGER.warn("[ALL TESTS ARE DISABLED] Cause: " + (reason.isPresent()));
        else {
            String testName = context.getRequiredTestMethod().getName();
            LOGGER.warn(">>>>> [TEST DISABLED]: " + testName + "; Cause:" + (reason.isPresent() ? reason : "not specified"));
            testCases.stream()
                    .filter(test -> test.getName().equals(testName))
                    .findFirst()
                    .ifPresent(test -> {
                        test.setTime("0.0");
                        test.setSkipped(new Skipped());
                    });
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = context.getRequiredTestMethod().getName();
        LOGGER.info(">>>>> [TEST COMPLETED]: " + testName);
        double duration = (System.currentTimeMillis() - testStarted) / 1000D;
        testCases.stream()
                .filter(testCase -> testCase.getName().equals(testName))
                .findFirst()
                .ifPresent(testCase -> testCase.setTime("" + duration));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {

    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = context.getRequiredTestMethod().getName();
        LOGGER.info(">>>>> [TEST FAILED]: " + testName);
        double duration = (System.currentTimeMillis() - testStarted) / 1000D;
        testCases.stream()
                .filter(testCase -> testCase.getName().equals(testName))
                .findFirst()
                .ifPresent(testCase -> {
                    testCase.setFailure(new Failure());
                    testCase.setTime("" + duration);
                });
    }

    private void addTestCase(Method method) {
        if (method.getName().isEmpty()) {
            LOGGER.info("[TEST CASE DOES NOT HAS METHOD NAME, PROBABLY PARAMETERIZED RUNNER USED]");
            LOGGER.info("[RESULT OF TEST CASE WITHOUT METHOD NAME WILL BE SKIPPED];");
        }

        TestCase testCase = new TestCase();
        testCase.setName(method.getName());
        testCase.setClassName(method.getDeclaringClass().getName());

        testCases.stream()
                .filter(tc -> tc.getName().equals(testCase.getName()))
                .findFirst()
                .ifPresent(tc -> {
                    testCases.remove(tc); //clean first if this the second test run
                    LOGGER.info(String.format("[TEST CASE WOITH CLASS NAME %s AND METHOD NAME %s IS FLAKE]",
                            testCase.getClassName(), testCase.getName()));
                });
        IssueKey issueKey = method.getDeclaredAnnotation(IssueKey.class);
        if (issueKey != null)
            testCase.setIssueKeys(asList(issueKey.value()));
        if (method.getDeclaredAnnotation(CreateTestIssue.class) != null)
            testCase.setCreateIssueInZephyr(true);

        testCases.add(testCase);
    }
}
